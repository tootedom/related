package org.greencheek.relatedproduct.indexing.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.greencheek.relatedproduct.searching.domain.RelatedProduct;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedProductIndexingRepository implements RelatedProductStorageRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedProductIndexingRepository.class);


    private final String indexType;
    private final String facetName;

    private final String idAttributeName;
    private final String dateAttributeName;

    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;


    public ElasticSearchRelatedProductIndexingRepository(Configuration configuration,
                                                         ElasticSearchClientFactory factory) {

        this.indexType = configuration.getStorageContentTypeName();
        this.idAttributeName = configuration.getKeyForIndexRequestIdAttr();
        this.dateAttributeName = configuration.getKeyForIndexRequestDateAttr();
        this.facetName = configuration.getRelatedWithFacetName();
        this.elasticSearchClientFactory = factory;
        this.elasticClient = elasticSearchClientFactory.getClient();
    }

    @Override
    public void store(RelatedProductStorageLocationMapper indexLocationMapper, RelatedProduct... relatedProducts) {
        BulkRequestBuilder bulkRequest = elasticClient.prepareBulk();


        int requestAdded = 0;
        for(RelatedProduct product : relatedProducts) {
            if(addRelatedProduct(indexLocationMapper,bulkRequest,product)) requestAdded++;
        }

        if(requestAdded>0) {
            log.info("Sending Relating Product Index Requests to Elastic: {}",requestAdded);
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

            if(bulkResponse.hasFailures()) {
                log.warn(bulkResponse.buildFailureMessage());
            }
        }
    }

    private boolean addRelatedProduct(RelatedProductStorageLocationMapper indexLocationMapper,
                                      BulkRequestBuilder bulkRequest,
                                      RelatedProduct product) {

        try {

            XContentBuilder builder = jsonBuilder().startObject()
                    .field(idAttributeName, product.getId())
                    .field(dateAttributeName, product.getDate())
                    .array(facetName, product.getRelatedProductPids());

            for(Map.Entry<String,String> property : product.getAdditionalProperties().entrySet()) {
                builder.field(property.getKey(),property.getValue());
            }

            builder.endObject();

            IndexRequestBuilder indexRequestBuilder = elasticClient.prepareIndex(indexLocationMapper.getLocationName(product), indexType);

            bulkRequest.add(indexRequestBuilder.setSource(builder));

            return true;
        } catch(IOException e) {
            return false;
        }

    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.debug("Shutting down ElasticSearchRelatedProductIndexingRepository");
        try {
            elasticSearchClientFactory.shutdown();
        } catch(Exception e) {

        }
    }
}
