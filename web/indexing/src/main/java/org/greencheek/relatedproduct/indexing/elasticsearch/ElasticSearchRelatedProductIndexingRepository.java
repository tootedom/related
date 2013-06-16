package org.greencheek.relatedproduct.indexing.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.util.UTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
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


    private static final String INDEX_NAME = "relatedpurchases-";
    private static final String INDEX_TYPE = "relatedproduct";

    private final Configuration configuration;
    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;
    private final UTCCurrentDateFormatter currentDayFormatter;


    public ElasticSearchRelatedProductIndexingRepository(Configuration configuration,
                                                         UTCCurrentDateFormatter currentDayFormatter,
                                                         ElasticSearchClientFactory factory) {
        this.configuration = configuration;
        this.elasticSearchClientFactory = factory;
        this.currentDayFormatter = currentDayFormatter;
        this.elasticClient = elasticSearchClientFactory.getClient();


    }

    @Override
    public void store(RelatedProduct... relatedProducts) {
        BulkRequestBuilder bulkRequest = elasticClient.prepareBulk();


        int requestAdded = 0;
        for(RelatedProduct product : relatedProducts) {
            if(addRelatedProduct(bulkRequest,product)) requestAdded++;
        }

        if(requestAdded>0) {
            log.info("Sending Relating Product Index Requests to Elastic: {}",requestAdded);
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();

            if(bulkResponse.hasFailures()) {
                log.warn(bulkResponse.buildFailureMessage());
            }
        }
    }

    private boolean addRelatedProduct(BulkRequestBuilder bulkRequest,
                                      RelatedProduct product) {

        StringBuilder indexName = new StringBuilder(27);
        indexName.append(INDEX_NAME).append(currentDayFormatter.parseToDate(product.getDate()));

        try {
            IndexRequestBuilder indexRequestBuilder = elasticClient.prepareIndex(indexName.toString(), INDEX_TYPE);

            XContentBuilder builder = jsonBuilder().startObject()
                    .field("id", product.getId())
                    .field("date", product.getDate())
                    .array(configuration.getRelatedWithFacetName(), product.getRelatedProductPids());

            for(Map.Entry<String,String> property : product.getAdditionalProperties().entrySet()) {
                builder.field(property.getKey(),property.getValue());
            }

            builder.endObject();

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
