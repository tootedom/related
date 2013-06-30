package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
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
import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.JsonFrequentlyRelatedSearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final String indexName;
    private final String facetResultName;
    private final long searchTimeout;

    public ElasticSearchFrequentlyRelatedProductSearchProcessor(Configuration configuration) {
        this.configuration = configuration;
        String indexNameAlias = configuration.getStorageIndexNameAlias();
        if(indexNameAlias==null || indexNameAlias.trim().length()==0) {
            indexName = configuration.getStorageIndexNamePrefix()+"*";
        } else {
            indexName = indexNameAlias;
        }
        this.facetResultName = configuration.getStorageFrequentlyRelatedProductsFacetResultsFacetName();
        this.searchTimeout = configuration.setFrequentlyRelatedProductsSearchTimeout();

    }

    public MultiSearchResponse executeSearch(Client elasticClient,RelatedProductSearch[] searches) {
        MultiSearchRequestBuilder multiSearch = elasticClient.prepareMultiSearch();
        for(RelatedProductSearch search : searches) {
            if(search.searchType.get() == RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
                multiSearch.add(createFrequentlyRelatedContentSearch(search,elasticClient));
            }
        }
        log.debug("executing searches");
        return multiSearch.execute().actionGet();
    }

    public Map<SearchRequestLookupKey,SearchResultsEvent> processMultiSearchResponse(RelatedProductSearch[] searches,MultiSearchResponse searchResponse) {

        List<ElasticSearchResponse> responses = new ArrayList<ElasticSearchResponse>(searches.length);
        for (MultiSearchResponse.Item item : searchResponse.getResponses()) {
            if(!item.isFailure()) {
                responses.add(new ElasticSearchResponse(item.getResponse()));
            } else {
                responses.add(new ElasticSearchResponse(item.getFailureMessage()));
            }
        }

        int numOfSearches = searches.length;
        Map<SearchRequestLookupKey,SearchResultsEvent> results = new HashMap<SearchRequestLookupKey,SearchResultsEvent>(searches.length);
        for(int i=0;i<numOfSearches;i++) {
            results.put(searches[i].getLookupKey(configuration),frequentlyRelatedWithResultsConverter(responses.get(i)));
        }

        return results;
    }

    private SearchResultsEvent frequentlyRelatedWithResultsConverter(ElasticSearchResponse response) {
        final FrequentlyRelatedSearchResults results;
        if(response.isFailure()) {
            results = FrequentlyRelatedSearchResults.EMPTY_RESULTS;
        }
        else {
            SearchResponse searchResponse = response.getSearchResponse();
            TermsFacet f = (TermsFacet) searchResponse.getFacets().facetsAsMap().get(facetResultName);
            List<TermsFacet.Entry> facets = (List<TermsFacet.Entry>) f.getEntries();
            int noOfFacets = facets==null ? 0 : facets.size();
            FrequentlyRelatedSearchResult[] mostFrequentItems = new FrequentlyRelatedSearchResult[noOfFacets];

            while(noOfFacets--!=0) {
                TermsFacet.Entry entry = facets.get(noOfFacets);
                mostFrequentItems[noOfFacets] = new FrequentlyRelatedSearchResult(entry.getTerm().string(), entry.getCount());
            }

            results = new FrequentlyRelatedSearchResults(mostFrequentItems);
        }

        return new SearchResultsEvent(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,results);
//        return new JsonFrequentlyRelatedSearchResultsConverter(configuration,results);

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
        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery(configuration.getKeyForIndexRequestIdAttr(),search.relatedContentId.get()));

        short numberOfProps = search.additionalSearchCriteria.numberOfProperties.get();
        for(int i = 0;i<numberOfProps;i++) {
            RelatedProductAdditionalProperty prop = search.additionalSearchCriteria.additionalProperties[i];
            bool.must(QueryBuilders.fieldQuery(prop.name.get(),prop.value.get()));
        }

        sr.setIndices(indexName);
        sr.setSize(0);
        sr.setQuery(bool);
        sr.setTimeout(TimeValue.timeValueMillis(searchTimeout));
        sr.addFacet(FacetBuilders.termsFacet(facetResultName).field(configuration.getKeyForIndexRequestRelatedWithAttr()).size(search.maxResults.get()));
        log.debug("Executing Query {}",sr);
        return sr;

    }
}
