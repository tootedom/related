/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing.elasticsearch;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.api.indexing.RelatedItemUtil;
import org.greencheek.related.elastic.http.*;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class ElasticSearchRelatedItemHttpIndexingRepository implements RelatedItemStorageRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemHttpIndexingRepository.class);


    private final String indexType;
    private final String relatedWithAttributeName;
    private final boolean threadedIndexing;
    private final IndexRequest.OpType createOrIndex;

    private final String idAttributeName;
    private final String dateAttributeName;

    private final HttpElasticClient elasticClient;

    private final String indexingEndpoint;


    private final String relatedItemsDocumentIndexName;
    private final String relatedItemsDocumentTypeName;
    private final String relatedItemsDocumentMergingScriptName;
    private final boolean relatedItemsDocumentIndexingEnabled;
    private final String relatedItemsDocumentComparisonKeyName;
    private int propertySize;
    private final boolean removeRelatedItemsDocumentDateAttribute;

    private final MessageDigest SHA256;


    public ElasticSearchRelatedItemHttpIndexingRepository(Configuration configuration,
                                                          HttpElasticSearchClientFactory factory) {

        this.indexType = configuration.getStorageContentTypeName();
        this.idAttributeName = configuration.getKeyForIndexRequestIdAttr();
        this.dateAttributeName = configuration.getKeyForIndexRequestDateAttr();
        this.relatedWithAttributeName = configuration.getKeyForIndexRequestRelatedWithAttr();
        this.createOrIndex = configuration.getShouldReplaceOldContentIfExists() == true ? IndexRequest.OpType.INDEX : IndexRequest.OpType.CREATE;
        this.threadedIndexing = configuration.getShouldUseSeparateIndexStorageThread();
        this.elasticClient = factory.getClient();
        this.propertySize = configuration.getRelatedItemAdditionalPropertyKeyLength()+configuration.getRelatedItemAdditionalPropertyValueLength();


        StringBuilder b = new StringBuilder(60);
        b.append("/_bulk?refresh=false&replication=async");
        indexingEndpoint = b.toString();


        this.relatedItemsDocumentIndexName = configuration.getRelatedItemsDocumentIndexName();
        this.relatedItemsDocumentMergingScriptName = configuration.getRelatedItemsDocumentMergingScriptName();
        this.relatedItemsDocumentTypeName =  configuration.getRelatedItemsDocumentTypeName();
        this.relatedItemsDocumentIndexingEnabled = configuration.getRelatedItemsDocumentIndexingEnabled();
        this.relatedItemsDocumentComparisonKeyName = configuration.getRelatedItemsDocumentComparisonKeyName();
        this.removeRelatedItemsDocumentDateAttribute = configuration.getRemoveRelatedItemsDocumentDateAttribute();

        try {
            SHA256 =  MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException e) {
            throw new InstantiationError();
        }
    }

    @Override
    public void store(RelatedItemStorageLocationMapper indexLocationMapper, List<RelatedItem> relatedItems) {
        StringBuilder jsonRequest = new StringBuilder(512*relatedItems.size());

        int requestAdded = 0;
        for(RelatedItem product : relatedItems) {
            int added = addRelatedItem(indexLocationMapper, jsonRequest, product);
            if(relatedItemsDocumentIndexingEnabled && added==1) {
                added += addRelatedItemDocument(jsonRequest, product);
            }
            requestAdded+=added;
        }

        if(requestAdded>0) {
            log.info("Sending {} Relating Product Index Requests to Elastic:",requestAdded);

            String request = jsonRequest.toString();
            log.debug("Sending http request {}",request);

            HttpResult res = elasticClient.executeSearch(HttpMethod.POST,indexingEndpoint,request);
            if(res.getStatus()!= HttpSearchExecutionStatus.OK) {
                String responseString = res.getResult();
                if(responseString!=null) {
                    log.warn("Bulk indexing request failed: {} with response {}",request,responseString);
                } else {
                    log.warn("Bulk indexing request failed: {}",request);
                }
            }
        }
    }

    private int addRelatedItemDocument(StringBuilder bulkRequest,
                                       RelatedItem product) {
        try {
            String id = new String(product.getId());
            XContentBuilder builder = jsonBuilder().startObject();
            String[][] props = RelatedItemUtil.getSortedProperties(product.getAdditionalProperties());
            for(String[] keyValue : props) {
                if(removeRelatedItemsDocumentDateAttribute && keyValue[0].equals(dateAttributeName)) {
                    continue;
                }

                builder.field(keyValue[0], keyValue[1]);

            }
            String sha256 = new String(RelatedItemUtil.getComparisonHashForRelatedItemProperties(props,propertySize,SHA256));
            builder.field(relatedItemsDocumentComparisonKeyName, sha256);
            builder.endObject();

            String document = builder.string();

            //{ "update" : {"_id" : "2", "_type" : "items", "_index" : "related"} }
            bulkRequest.append("{\"update\":");
            bulkRequest.append("{\"_id\":\"").append(id).append("\",");
            bulkRequest.append("\"_index\":\"").append(relatedItemsDocumentIndexName);
            bulkRequest.append("\",\"_type\":\"").append(relatedItemsDocumentTypeName);
            bulkRequest.append("\"}}\n");

            // { "script" : "foo","lang" : "native","params" : {"md5" : "ttffff","xxx" : "xxx"},"upsert":{"id":"2","date":"2013-12-24T17:44:41.943Z","md5":"tt","type":"map","site":"amazon","channel":"de" } }
            bulkRequest.append("{\"script\":\"").append(relatedItemsDocumentMergingScriptName).append("\",");
            bulkRequest.append("\"lang\":\"native\",\"params\":");
            bulkRequest.append(document);
            bulkRequest.append(",\"upsert\":");
            bulkRequest.append(document).append("}\n");


            return 1;
        } catch (IOException e) {
            return 0;
        }
    }

    private int addRelatedItem(RelatedItemStorageLocationMapper indexLocationMapper,
                               StringBuilder bulkRequest,
                               RelatedItem product) {

        try {

            char[] id = product.getId();
            XContentBuilder builder = jsonBuilder().startObject()
                    .field(idAttributeName,id,0,id.length )
                    .field(dateAttributeName, product.getDate()).startArray(relatedWithAttributeName);

            for(char[] relatedIds : product.getRelatedItemPids()) {
                builder.value(new String(relatedIds, 0, relatedIds.length));
            }
            builder.endArray();

            RelatedItemAdditionalProperties properties = product.getAdditionalProperties();
            int maxNumberOfProperties = properties.getNumberOfProperties();
            for(int i=0;i<maxNumberOfProperties;i++) {
                char[] value = properties.getPropertyValueCharArray(i);
                builder.field(properties.getPropertyName(i), value, 0, value.length);
            }

            builder.endObject();

            if(createOrIndex== IndexRequest.OpType.INDEX) {
                bulkRequest.append("{\"index\":{");
            } else {
                bulkRequest.append("{\"create\":{");
            }
            bulkRequest.append("\"_index\":\"").append(indexLocationMapper.getLocationName(product));
            bulkRequest.append("\",\"_type\":\"").append(indexType);
            bulkRequest.append("\"}}\n");
            bulkRequest.append(builder.string()).append("\n");

            if(log.isDebugEnabled()) {
                log.debug("added indexing request to batch request: {}", builder.string());
            }

            return 1;
        } catch(IOException e) {
            return 0;
        }

    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down ElasticSearchRelatedItemIndexingRepository");
        try {
            elasticClient.shutdown();
        } catch(Exception e) {

        }
    }
}
