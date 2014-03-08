package org.greencheek.related.searching.repository;

import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.Client;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.searching.RelatedItemGetRepository;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dominictootell on 05/03/2014.
 */
public class ElasticSearchRelatedItemGetRepository implements RelatedItemGetRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemGetRepository.class);


    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;

    private final String indexName;
    private final String typeName;
    private final long searchTimeout;
    private final static String EMPTY_RESULT = "{}";
    private final String timingKey;


    public ElasticSearchRelatedItemGetRepository(Configuration configuration,
                                                 ElasticSearchClientFactory searchClientFactory) {
        this.elasticSearchClientFactory = searchClientFactory;
        this.elasticClient = elasticSearchClientFactory.getClient();
        this.indexName = configuration.getRelatedItemsDocumentIndexName();
        this.typeName = configuration.getRelatedItemsDocumentTypeName();
        this.searchTimeout = configuration.getFrequentlyRelatedItemsSearchTimeoutInMillis();
        this.timingKey = configuration.getKeyForStorageGetResponseTime();
    }


    private final void populateEmptyResults(String[] ids,Map<String,String> results) {
        for(String id: ids) {
            results.put(id,EMPTY_RESULT);
        }
    }

    @Override
    public Map<String, String> getRelatedItemDocument(String[] ids) {
        Map<String,String> results = new HashMap<String,String>(ids.length);
        populateEmptyResults(ids,results);

        MultiGetRequestBuilder get = elasticClient.prepareMultiGet().add(indexName, typeName, ids);
        long startNanos = System.nanoTime();
        try {
            MultiGetResponse response = get.execute().actionGet(searchTimeout, TimeUnit.MILLISECONDS);
            MultiGetItemResponse[] items = response.getResponses();
            for(MultiGetItemResponse item : items) {
                if(!item.isFailed()) {
                    results.put(item.getId(), item.getResponse().getSourceAsString());
                }
            } 
        }
        catch(ElasticsearchTimeoutException timeoutException) {

        }
        catch (Exception e) {

        }

        results.put(timingKey,Long.toString((System.nanoTime()-startNanos)/1000000));
        return results;
    }

    @Override
    public void shutdown() {
        try {
            elasticSearchClientFactory.shutdown();
        } catch(Exception e) {
            log.warn("unable to stop ");
        }
    }
}
