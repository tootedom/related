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
    private final FrequentRelatedSearchRequestBuilder builder;
//    private final Map<String,SearchFieldType> searchFieldType;

    public ElasticSearchFrequentlyRelatedItemSearchProcessor(Configuration configuration,
                                                             FrequentRelatedSearchRequestBuilder builder) {
        this.builder = builder;
        this.configuration = configuration;
        String indexNameAlias = configuration.getStorageIndexNameAlias();
        if(indexNameAlias==null || indexNameAlias.trim().length()==0) {
            indexName = configuration.getStorageIndexNamePrefix()+"*";
        } else {
            indexName = indexNameAlias;
        }
        this.facetResultName = configuration.getStorageFrequentlyRelatedItemsFacetResultsFacetName();
        this.searchTimeout = configuration.getFrequentlyRelatedItemsSearchTimeoutInMillis();
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



    /*
     Creates a query like:

        {
          "size" : 0,
          "timeout" : 5000,
          "query" : {
            "constant_score" : {
              "filter" : {
                "bool" : {
                  "must" : [ {
                    "term" : {
                      "related-with" : "apparentice you're hired"
                    }
                  }, {
                    "term" : {
                      "channel" : "bbc"
                    }
                  } ]
                }
              }
            }
          },
          "facets" : {
            "frequently-related-with" : {
              "terms" : {
                "field" : "id",
                "size" : 5,
                "execution_hint" : "map"
              }
            }
          }
        }
     */
    private SearchRequestBuilder createFrequentlyRelatedContentSearch(RelatedItemSearch search, Client searchClient) {
        SearchRequestBuilder sr = searchClient.prepareSearch();
        sr.internalBuilder(builder.createFrequentlyRelatedContentSearch(search));
        sr.setIndices(indexName);
        return sr;

    }
}
