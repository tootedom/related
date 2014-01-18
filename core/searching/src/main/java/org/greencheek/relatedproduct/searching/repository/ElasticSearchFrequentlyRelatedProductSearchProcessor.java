package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.searching.*;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 19:47
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchFrequentlyRelatedProductSearchProcessor implements MultiSearchResponseProcessor<FrequentlyRelatedSearchResult[]> {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchFrequentlyRelatedProductSearchProcessor.class);

    private final Configuration configuration;
    private final String indexName;
    private final String facetResultName;
    private final long searchTimeout;
    private final String executionHint;
    private final boolean hasExecutionHint;
//    private final Map<String,SearchFieldType> searchFieldType;

    public ElasticSearchFrequentlyRelatedProductSearchProcessor(Configuration configuration) {
        this.configuration = configuration;
        String indexNameAlias = configuration.getStorageIndexNameAlias();
        if(indexNameAlias==null || indexNameAlias.trim().length()==0) {
            indexName = configuration.getStorageIndexNamePrefix()+"*";
        } else {
            indexName = indexNameAlias;
        }
        this.facetResultName = configuration.getStorageFrequentlyRelatedProductsFacetResultsFacetName();
        this.searchTimeout = configuration.getFrequentlyRelatedProductsSearchTimeoutInMillis();

        String executionHint = configuration.getStorageFacetExecutionHint();

        if(executionHint == null) {
            hasExecutionHint = false;
        } else {
            executionHint = executionHint.trim();
            if(executionHint.length()==0) {
                hasExecutionHint = false;
            } else {
                hasExecutionHint=true;
            }
        }

        this.executionHint = executionHint;
    }

    public MultiSearchResponse executeSearch(Client elasticClient,RelatedProductSearch[] searches) {
        MultiSearchRequestBuilder multiSearch = elasticClient.prepareMultiSearch();
        for(RelatedProductSearch search : searches) {

            if(search.getRelatedProductSearchType() == RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
                multiSearch.add(createFrequentlyRelatedContentSearch(search,elasticClient));
            }
        }
        log.debug("executing search {} request(s)",searches.length);
        return multiSearch.execute().actionGet(searchTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]>[] processMultiSearchResponse(RelatedProductSearch[] searches,MultiSearchResponse searchResponse) {
        int i = 0;
        SearchResultEventWithSearchRequestKey[] results = new SearchResultEventWithSearchRequestKey[searches.length];
        for(MultiSearchResponse.Item item : searchResponse.getResponses()) {
            SearchRequestLookupKey key = searches[i].getLookupKey();
            results[i++] = frequentlyRelatedWithResultsConverter(key,item.getResponse(),item.getFailureMessage(),item.isFailure());
        }

        return results;
    }

    private SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> frequentlyRelatedWithResultsConverter(SearchRequestLookupKey key,
                                                                     SearchResponse searchResponse,
                                                                     String failureMessage,
                                                                     boolean isFailure) {
        final FrequentlyRelatedSearchResult[] results;
        SearchResultsOutcome outcome;
        if(isFailure) {
            log.error("Search response failure for search request key : {}",key,failureMessage);
            return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key);
        }
        else {
            TermsFacet f = (TermsFacet) searchResponse.getFacets().facetsAsMap().get(facetResultName);
            List<TermsFacet.Entry> facets = (List<TermsFacet.Entry>) f.getEntries();
            int noOfFacets = facets==null ? 0 : facets.size();
            if(noOfFacets!=0) {
                results = new FrequentlyRelatedSearchResult[noOfFacets];

                while(noOfFacets--!=0) {
                    TermsFacet.Entry entry = facets.get(noOfFacets);
                    results[noOfFacets] = new FrequentlyRelatedSearchResult(entry.getTerm().string(), entry.getCount());
                }
                outcome = SearchResultsOutcome.HAS_RESULTS;
            } else {
                log.debug("no related content found for search key {}",key);
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS,key);
            }

        }

        return new SearchResultEventWithSearchRequestKey(new SearchResultsEvent(outcome,results),
                                                         key);

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
//    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedProductSearch search, Client searchClient) {
//        SearchRequestBuilder sr = searchClient.prepareSearch();
//        String id = search.getRelatedContentId();
//        StringBuilder b = new StringBuilder(id.length()+2).append('"').append(id).append('"');
//        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery(configuration.getKeyForIndexRequestIdAttr(),b.toString()));
//
//        RelatedProductAdditionalProperties searchProps = search.getAdditionalSearchCriteria();
//        int numberOfProps = searchProps.getNumberOfProperties();
//
//        for(int i = 0;i<numberOfProps;i++) {
//            bool.must(QueryBuilders.fieldQuery(searchProps.getPropertyName(i), searchProps.getPropertyValue(i)));
//        }
//
//        sr.setIndices(indexName);
//        sr.setSize(0);
//        sr.setQuery(bool);
//        sr.setTimeout(TimeValue.timeValueMillis(searchTimeout));
//        sr.addFacet(FacetBuilders.termsFacet(facetResultName).field(configuration.getKeyForIndexRequestRelatedWithAttr()).size(search.getMaxResults()));
//        log.debug("Executing Query {}",sr);
//        return sr;
//
//    }

    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedProductSearch search, Client searchClient) {
        String id = search.getRelatedContentId();

//        BoolQueryBuilder b = QueryBuilders.boolQuery();

        BoolFilterBuilder b = FilterBuilders.boolFilter();
        b.must(FilterBuilders.termFilter(configuration.getKeyForIndexRequestIdAttr(), id));

        SearchRequestBuilder sr = searchClient.prepareSearch();

//        StringBuilder b = new StringBuilder(id.length()+2).append('"').append(id).append('"');
//        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery(configuration.getKeyForIndexRequestIdAttr(),b.toString()));

        RelatedProductAdditionalProperties searchProps = search.getAdditionalSearchCriteria();
        int numberOfProps = searchProps.getNumberOfProperties();

        for(int i = 0;i<numberOfProps;i++) {
            b.must(FilterBuilders.termFilter(searchProps.getPropertyName(i), searchProps.getPropertyValue(i)));
        }
        ConstantScoreQueryBuilder cs = QueryBuilders.constantScoreQuery(b);


        TermsFacetBuilder facetBuilder = FacetBuilders.termsFacet(facetResultName).field(configuration.getKeyForIndexRequestRelatedWithAttr()).size(search.getMaxResults());
        if(hasExecutionHint) {
            facetBuilder.executionHint(executionHint);
        }

        sr.setIndices(indexName);
        sr.setSize(0);
        sr.setQuery(cs);
        sr.setTimeout(TimeValue.timeValueMillis(searchTimeout));
        sr.addFacet(facetBuilder);
        log.debug("Executing Query {}",sr);
        return sr;

    }
}
