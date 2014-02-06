/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.searching.repository;

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
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.searching.*;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Creates the search request and executes it against elastic search.
 */
public class ElasticSearchFrequentlyRelatedItemSearchProcessor implements MultiSearchResponseProcessor<FrequentlyRelatedSearchResult[]> {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchFrequentlyRelatedItemSearchProcessor.class);

    private final Configuration configuration;
    private final String indexName;
    private final String facetResultName;
    private final long searchTimeout;
    private final TimeValue searchTimeoutValue;
    private final String executionHint;
    private final boolean hasExecutionHint;
    private final String relatedWithAttribute;
    private final String itemIdentifierAttribute;
//    private final Map<String,SearchFieldType> searchFieldType;

    public ElasticSearchFrequentlyRelatedItemSearchProcessor(Configuration configuration) {
        this.configuration = configuration;
        String indexNameAlias = configuration.getStorageIndexNameAlias();
        if(indexNameAlias==null || indexNameAlias.trim().length()==0) {
            indexName = configuration.getStorageIndexNamePrefix()+"*";
        } else {
            indexName = indexNameAlias;
        }
        this.facetResultName = configuration.getStorageFrequentlyRelatedItemsFacetResultsFacetName();
        this.searchTimeout = configuration.getFrequentlyRelatedItemsSearchTimeoutInMillis();
        this.searchTimeoutValue = TimeValue.timeValueMillis(searchTimeout);

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

        this.relatedWithAttribute = configuration.getKeyForIndexRequestRelatedWithAttr();
        this.itemIdentifierAttribute = configuration.getKeyForIndexRequestIdAttr();
    }

    public MultiSearchResponse executeSearch(Client elasticClient,RelatedItemSearch[] searches) {
        MultiSearchRequestBuilder multiSearch = elasticClient.prepareMultiSearch();
        for(RelatedItemSearch search : searches) {

            if(search.getRelatedItemSearchType() == RelatedItemSearchType.FREQUENTLY_RELATED_WITH) {
                multiSearch.add(createFrequentlyRelatedContentSearch(search,elasticClient));
            }
        }
        log.debug("executing search {} request(s)",searches.length);
        return multiSearch.execute().actionGet(searchTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]>[] processMultiSearchResponse(RelatedItemSearch[] searches,MultiSearchResponse searchResponse) {
        int i = 0;
        SearchResultEventWithSearchRequestKey[] results = new SearchResultEventWithSearchRequestKey[searches.length];
        for(MultiSearchResponse.Item item : searchResponse.getResponses()) {
            SearchRequestLookupKey key = searches[i].getLookupKey();
            results[i] = frequentlyRelatedWithResultsConverter(key,item.getResponse(),item.getFailureMessage(),item.isFailure(),searches[i++].getStartOfRequestNanos());
        }

        return results;
    }

    private SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> frequentlyRelatedWithResultsConverter(SearchRequestLookupKey key,
                                                                     SearchResponse searchResponse,
                                                                     String failureMessage,
                                                                     boolean isFailure,
                                                                     long requestStartTime) {
        final FrequentlyRelatedSearchResult[] results;
        SearchResultsOutcome outcome;
        if(isFailure) {
            log.error("Search response failure for search request key : {}",key,failureMessage);
            if(searchResponse!=null) {
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key,searchResponse.getTookInMillis(),requestStartTime);
            } else {
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key,-1,requestStartTime);
            }
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
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS,key,searchResponse.getTookInMillis(),requestStartTime);
            }

        }

        return new SearchResultEventWithSearchRequestKey(new SearchResultsEvent(outcome,results),
                                                         key,searchResponse.getTookInMillis(),requestStartTime);

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
//    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedItemSearch search, Client searchClient) {
//        SearchRequestBuilder sr = searchClient.prepareSearch();
//        String id = search.getRelatedItemId();
//        StringBuilder b = new StringBuilder(id.length()+2).append('"').append(id).append('"');
//        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery(configuration.getKeyForIndexRequestIdAttr(),b.toString()));
//
//        RelatedItemAdditionalProperties searchProps = search.getAdditionalSearchCriteria();
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

    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedItemSearch search, Client searchClient) {
        String id = search.getRelatedItemId();

//        BoolQueryBuilder b = QueryBuilders.boolQuery();

        BoolFilterBuilder b = FilterBuilders.boolFilter();
        b.must(FilterBuilders.termFilter(relatedWithAttribute, id));

        SearchRequestBuilder sr = searchClient.prepareSearch();

//        StringBuilder b = new StringBuilder(id.length()+2).append('"').append(id).append('"');
//        BoolQueryBuilder bool = QueryBuilders.boolQuery().must(QueryBuilders.fieldQuery(configuration.getKeyForIndexRequestIdAttr(),b.toString()));

        RelatedItemAdditionalProperties searchProps = search.getAdditionalSearchCriteria();
        int numberOfProps = searchProps.getNumberOfProperties();

        for(int i = 0;i<numberOfProps;i++) {
            b.must(FilterBuilders.termFilter(searchProps.getPropertyName(i), searchProps.getPropertyValue(i)));
        }
        ConstantScoreQueryBuilder cs = QueryBuilders.constantScoreQuery(b);


        TermsFacetBuilder facetBuilder = FacetBuilders.termsFacet(facetResultName).field(itemIdentifierAttribute).size(search.getMaxResults());
        if(hasExecutionHint) {
            facetBuilder.executionHint(executionHint);
        }

        sr.setIndices(indexName);
        sr.setSize(0);
        sr.setQuery(cs);
        sr.setTimeout(searchTimeoutValue);
        sr.addFacet(facetBuilder);
        log.debug("Executing Query {}",sr);
        return sr;

    }
}
