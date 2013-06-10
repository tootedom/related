package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.Client;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.SearchRequestResponseHandler;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
@Named
public class ElasticSearchRelatedProductSearchRepository implements RelatedProductSearchRepository {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedProductSearchRepository.class);

    private static final String INDEX_ALIAS_NAME = "relatedpurchases";
    private static final String INDEX_TYPE = "relatedproduct";

    private static final String FACET_RESULT_NAME ="frequently-related-with";
    private final String relatedWithFacetName;

    private final ElasticSearchClientFactory elasticSearchClientFactory;
    private final Client elasticClient;
    private final Configuration configuration;
    private final ElasticSearchFrequentlyRelatedProductSearchProcessor frequentlyRelatedWithSearchBuilder;

    @Inject
    public ElasticSearchRelatedProductSearchRepository(Configuration configuration,
                                                       ElasticSearchClientFactory searchClientFactory,
                                                       ElasticSearchFrequentlyRelatedProductSearchProcessor builder) {
        this.configuration = configuration;
        this.relatedWithFacetName = configuration.getRelatedWithFacetName();
        this.elasticSearchClientFactory = searchClientFactory;
        this.elasticClient = elasticSearchClientFactory.getClient();
        this.frequentlyRelatedWithSearchBuilder = builder;
    }


    @Override
    public void findRelatedProducts(RelatedProductSearch[] searches, SearchRequestResponseHandler handler) {

        MultiSearchResponse sr = frequentlyRelatedWithSearchBuilder.executeSearch(elasticClient,searches);

        Map<SearchRequestLookupKey,SearchResultsConverter> results = frequentlyRelatedWithSearchBuilder.processMultiSearchResponse(searches,sr);


        for(Map.Entry<SearchRequestLookupKey,SearchResultsConverter> entry : results.entrySet()) {
            handler.handleResponse(entry.getKey(),entry.getValue());
        }

    }

}
