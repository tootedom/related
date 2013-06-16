package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.JsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchFrequentlyRelatedProductSearchProcessor {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchFrequentlyRelatedProductSearchProcessor.class);


    private final Configuration configuration;
    private static final String FACET_RESULT_NAME ="frequently-related-with";
    private final String relatedWithFacetName;

    public ElasticSearchFrequentlyRelatedProductSearchProcessor(Configuration configuration) {
        this.configuration = configuration;
        this.relatedWithFacetName = configuration.getRelatedWithFacetName();

    }

    public MultiSearchResponse executeSearch(Client elasticClient,RelatedProductSearch[] searches
                                             ) {
        MultiSearchRequestBuilder multiSearch = elasticClient.prepareMultiSearch();
        for(RelatedProductSearch search : searches) {
            if(search.searchType.get() == RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
                multiSearch.add(createFrequentlyRelatedContentSearch(search,elasticClient));
            }
        }
        log.debug("executing searches");
        return multiSearch.execute().actionGet();
    }

    public Map<SearchRequestLookupKey,SearchResultsConverter> processMultiSearchResponse(RelatedProductSearch[] searches,MultiSearchResponse searchResponse) {

        List<ElasticSearchResponse> responses = new ArrayList<ElasticSearchResponse>(searches.length);
        for (MultiSearchResponse.Item item : searchResponse.getResponses()) {
            if(!item.isFailure()) {
                responses.add(new ElasticSearchResponse(item.getResponse()));
            } else {
                responses.add(new ElasticSearchResponse(item.getFailureMessage()));
            }
        }

        int numOfSearches = searches.length;
        Map<SearchRequestLookupKey,SearchResultsConverter> results = new HashMap<SearchRequestLookupKey,SearchResultsConverter>(searches.length);
        for(int i=0;i<numOfSearches;i++) {
            results.put(searches[i].getLookupKey(configuration),frequentlyRelatedWithResultsConverter(responses.get(i)));
        }

        return results;
    }

    private SearchResultsConverter frequentlyRelatedWithResultsConverter(ElasticSearchResponse response) {
        final FrequentlyRelatedSearchResults results;
        if(response.isFailure()) {
            results = new FrequentlyRelatedSearchResults(Collections.EMPTY_LIST);
        }
        else {
            SearchResponse searchResponse = response.getSearchResponse();
            TermsFacet f = (TermsFacet) searchResponse.getFacets().facetsAsMap().get(FACET_RESULT_NAME);
            List<FrequentlyRelatedSearchResult> mostFrequentItems = new ArrayList<FrequentlyRelatedSearchResult>((int)f.getTotalCount());

            for(TermsFacet.Entry entry : f) {

                mostFrequentItems.add(new FrequentlyRelatedSearchResult(entry.getTerm().string(), entry.getCount()));
            }

            results = new FrequentlyRelatedSearchResults(mostFrequentItems);
        }

        return new JsonFrequentlyRelatedSearchResultsConverter(configuration,results);

    }



    /**
     * Search like :
     *
     * "query" : {
            "bool" : {
                "must" : [
                    {"field" : {"id" : "338906"}},
                    {"field" : {"channel" : "uk"}},
                    {"field" : {"site" : "amazon"}}
                ]
            }
        },
        "facets" : {
            "frequently-related-with" : {
                "terms" : {"field" : "related-with", "size" : 5 }
            }
        },
        "size":0
     }'
     * @param search
     * @return
     */
    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedProductSearch search, Client searchClient) {


        SearchRequestBuilder sr = searchClient.prepareSearch();
        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery("id",search.relatedContentId.get()));

        short numberOfProps = search.additionalSearchCriteria.numberOfProperties.get();
        for(int i = 0;i<numberOfProps;i++) {
            RelatedProductAdditionalProperty prop = search.additionalSearchCriteria.additionalProperties[i];
            bool.must(QueryBuilders.fieldQuery(prop.name.get(),prop.value.get()));
        }

        sr.setQuery(bool);
        sr.addFacet(FacetBuilders.termsFacet(FACET_RESULT_NAME).field(relatedWithFacetName).size(search.maxResults.get()));

        return sr;

    }
}
