package org.greencheek.related.searching.repository;

import org.elasticsearch.ElasticSearchTimeoutException;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.searching.RelatedItemSearchRepository;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedItemSearchRepository implements RelatedItemSearchRepository<FrequentlyRelatedSearchResult[]> {


    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemSearchRepository.class);

    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;
    private final ElasticSearchFrequentlyRelatedItemSearchProcessor frequentlyRelatedWithSearchBuilder;

    public ElasticSearchRelatedItemSearchRepository(ElasticSearchClientFactory searchClientFactory,
                                                    ElasticSearchFrequentlyRelatedItemSearchProcessor builder) {
        this.elasticSearchClientFactory = searchClientFactory;
        this.elasticClient = elasticSearchClientFactory.getClient();
        this.frequentlyRelatedWithSearchBuilder = builder;
    }


    @Override
    public SearchResultEventWithSearchRequestKey[] findRelatedItems(Configuration configuration,
                                                                    RelatedItemSearch[] searches) {
        log.debug("request to execute {} searches",searches.length);
        SearchResultEventWithSearchRequestKey[] results;
        MultiSearchResponse sr;
        try {
            sr = frequentlyRelatedWithSearchBuilder.executeSearch(elasticClient,searches);
            log.debug("Processing results for search {} request(s)",searches.length);
            results = frequentlyRelatedWithSearchBuilder.processMultiSearchResponse(searches,sr);
            log.debug("Search Completed, returning processed results.");
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
