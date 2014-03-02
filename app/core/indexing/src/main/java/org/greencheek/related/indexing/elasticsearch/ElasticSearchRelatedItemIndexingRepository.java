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

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.support.replication.ReplicationType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.api.indexing.RelatedItemUtil;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class ElasticSearchRelatedItemIndexingRepository implements RelatedItemStorageRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemIndexingRepository.class);


    private final String indexType;
    private final String relatedWithAttributeName;
    private final int propertySize;
    private final boolean threadedIndexing;
    private final IndexRequest.OpType createOrIndex;

    private final String idAttributeName;
    private final String dateAttributeName;

    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;

    private final String relatedItemsDocumentIndexName;
    private final String relatedItemsDocumentTypeName;
    private final String relatedItemsDocumentMergingScriptName;
    private final boolean relatedItemsDocumentIndexingEnabled;
    private final String relatedItemsDocumentMD5KeyName;

    private final MessageDigest MD5;

    public ElasticSearchRelatedItemIndexingRepository(Configuration configuration,
                                                      ElasticSearchClientFactory factory) {

        this.indexType = configuration.getStorageContentTypeName();
        this.idAttributeName = configuration.getKeyForIndexRequestIdAttr();
        this.dateAttributeName = configuration.getKeyForIndexRequestDateAttr();
        this.relatedWithAttributeName = configuration.getKeyForIndexRequestRelatedWithAttr();
        this.createOrIndex = configuration.getShouldReplaceOldContentIfExists() == true ? IndexRequest.OpType.INDEX : IndexRequest.OpType.CREATE;
        this.threadedIndexing = configuration.getShouldUseSeparateIndexStorageThread();
        this.propertySize = configuration.getRelatedItemAdditionalPropertyKeyLength()+configuration.getRelatedItemAdditionalPropertyValueLength();
        this.elasticSearchClientFactory = factory;
        this.elasticClient = elasticSearchClientFactory.getClient();

        this.relatedItemsDocumentIndexName = configuration.getRelatedItemsDocumentIndexName();
        this.relatedItemsDocumentMergingScriptName = configuration.getRelatedItemsDocumentMergingScriptName();
        this.relatedItemsDocumentTypeName =  configuration.getRelatedItemsDocumentTypeName();
        this.relatedItemsDocumentIndexingEnabled = configuration.getRelatedItemsDocumentIndexingEnabled();
        this.relatedItemsDocumentMD5KeyName = configuration.getRelatedItemsDocumentMD5KeyName();

        try {
            MD5 =  MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException e) {
            throw new InstantiationError();
        }
    }

    @Override
    public void store(RelatedItemStorageLocationMapper indexLocationMapper, List<RelatedItem> relatedItems) {
        BulkRequestBuilder bulkRequest = elasticClient.prepareBulk();
        bulkRequest.setReplicationType(ReplicationType.ASYNC).setRefresh(false);

        int requestAdded = 0;
        for(RelatedItem product : relatedItems) {
            int added = addRelatedItem(indexLocationMapper, bulkRequest, product);
            if(relatedItemsDocumentIndexingEnabled && added==1) {
                added+=addRelatedItemDocument(bulkRequest, product);
            }
            requestAdded+=added;
        }

        if(requestAdded>0) {
            log.info("Sending Relating Product Index Requests to Elastic: {}",requestAdded);
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

            if(bulkResponse.hasFailures()) {
                log.warn(bulkResponse.buildFailureMessage());
            }
        }
    }



    private int addRelatedItemDocument(BulkRequestBuilder bulkRequestBuilder,
                                       RelatedItem product) {
        try {
            String id = new String(product.getId());

            UpdateRequestBuilder updateRequestBuilder = elasticClient.prepareUpdate(relatedItemsDocumentIndexName, relatedItemsDocumentTypeName, id);
            updateRequestBuilder.setId(id);

            XContentBuilder builder = jsonBuilder().startObject();

            String[][] props = RelatedItemUtil.getSortedProperties(product.getAdditionalProperties());
            for(String[] keyValue : props) {
                builder.field(keyValue[0],keyValue[1]);
                updateRequestBuilder.addScriptParam(keyValue[0],keyValue[1]);
            }
            updateRequestBuilder.setScriptLang("native");
            String md5 = new String(RelatedItemUtil.getMD5ForRelatedItemProperties(props,propertySize,MD5));
            updateRequestBuilder.addScriptParam(relatedItemsDocumentMD5KeyName, md5);
            updateRequestBuilder.setScript(relatedItemsDocumentMergingScriptName);

            builder.field(relatedItemsDocumentMD5KeyName, md5);
            builder.endObject();
            updateRequestBuilder.setUpsert(builder);
            bulkRequestBuilder.add(updateRequestBuilder);
            return 1;
        } catch (IOException e) {
            return 0;
        }
    }

    private int addRelatedItem(RelatedItemStorageLocationMapper indexLocationMapper,
                               BulkRequestBuilder bulkRequest,
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


            IndexRequestBuilder indexRequestBuilder = elasticClient.prepareIndex(indexLocationMapper.getLocationName(product), indexType);
            indexRequestBuilder.setOpType(createOrIndex);
            indexRequestBuilder.setOperationThreaded(threadedIndexing);
            if(log.isDebugEnabled()) {
                log.debug("added indexing request to batch request: {}", builder.string());
            }

            bulkRequest.add(indexRequestBuilder.setSource(builder));
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
            elasticSearchClientFactory.shutdown();
        } catch(Exception e) {

        }
    }
}
