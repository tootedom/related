package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.ElasticSearchTimeoutException;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedProductSearchRepository implements RelatedProductSearchRepository {


    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedProductSearchRepository.class);

    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;
    private final ElasticSearchFrequentlyRelatedProductSearchProcessor frequentlyRelatedWithSearchBuilder;

    public ElasticSearchRelatedProductSearchRepository(ElasticSearchClientFactory searchClientFactory,
                                                       ElasticSearchFrequentlyRelatedProductSearchProcessor builder) {
        this.elasticSearchClientFactory = searchClientFactory;
        this.elasticClient = elasticSearchClientFactory.getClient();
        this.frequentlyRelatedWithSearchBuilder = builder;
    }


    @Override
    public SearchResultEventWithSearchRequestKey[] findRelatedProducts(Configuration configuration,
                                    RelatedProductSearch[] searches) {
        log.debug("request to execute {} searches",searches.length);
        SearchResultEventWithSearchRequestKey[] results;
        MultiSearchResponse sr;
        try {
            sr = frequentlyRelatedWithSearchBuilder.executeSearch(elasticClient,searches);
            results = frequentlyRelatedWithSearchBuilder.processMultiSearchResponse(searches,sr);
        } catch(ElasticSearchTimeoutException timeoutException) {
            log.warn("Timeout exception executing search request: ",timeoutException);
            int size = searches.length;
            results = new SearchResultEventWithSearchRequestKey[size];

            for(int i=0;i<size;i++) {
                SearchRequestLookupKey key = searches[i].getLookupKey();
                results[i] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS,key);
            }
        } catch(Exception searchException) {
            log.warn("Exception executing search request: ",searchException);
            int size = searches.length;
            results = new SearchResultEventWithSearchRequestKey[size];
            for(int i=0;i<size;i++) {
                SearchRequestLookupKey key = searches[i].getLookupKey();
                results[i] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key);
            }

        }

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
