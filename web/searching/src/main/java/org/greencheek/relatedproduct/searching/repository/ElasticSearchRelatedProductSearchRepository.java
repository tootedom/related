package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
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
    public void findRelatedProducts(Configuration configuration,
                                    RelatedProductSearch[] searches,
                                    RelatedProductSearchRequestResponseProcessor handler) {
        log.debug("request to execute {} searches",searches.length);
        Map<SearchRequestLookupKey,SearchResultsEvent> results;
        MultiSearchResponse sr;
        try {
            sr = frequentlyRelatedWithSearchBuilder.executeSearch(elasticClient,searches);
            results = frequentlyRelatedWithSearchBuilder.processMultiSearchResponse(searches,sr);
        } catch(Exception searchException) {
            int size = searches.length;
            results = new HashMap<SearchRequestLookupKey,SearchResultsEvent>(size);
            int i = size+1;
            while(i--!=0) {
                SearchRequestLookupKey key = searches[i].getLookupKey(configuration);
                results.put(key,SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS);
            }

        }




        for(Map.Entry<SearchRequestLookupKey,SearchResultsEvent> entry : results.entrySet()) {
            handler.handleResponse(entry.getKey(),entry.getValue());
        }

    }

}
