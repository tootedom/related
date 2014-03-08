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

package org.greencheek.related.searching.repository.http;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.elastic.http.HttpElasticClient;
import org.greencheek.related.elastic.http.HttpMethod;
import org.greencheek.related.elastic.http.HttpResult;
import org.greencheek.related.searching.RelatedItemGetRepository;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.repository.FrequentRelatedSearchRequestBuilder;
import org.greencheek.related.searching.repository.MultiSearchResponseProcessor;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Creates the search request and executes it against elastic search.
 */
public class ElasticSearchFrequentlyRelatedItemHttpSearchProcessor {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchFrequentlyRelatedItemHttpSearchProcessor.class);

    private final Configuration configuration;
    private final String indexName;
    private final String typeName;
    private final String msearchHeader;
    private final String msearchEndpoint;

    private final FrequentRelatedSearchRequestBuilder relatedItemSearchBuilder;
    private final FrequentlyRelatedItemHttpResponseParser responseParser;
    private final RelatedItemGetRepository getRepository;

//    private final Map<String,SearchFieldType> searchFieldType;

    public ElasticSearchFrequentlyRelatedItemHttpSearchProcessor(Configuration configuration,
                                                                 FrequentRelatedSearchRequestBuilder builder,
                                                                 FrequentlyRelatedItemHttpResponseParser responseParser,
                                                                 RelatedItemGetRepository getRepository) {
        this.responseParser = responseParser;
        this.msearchEndpoint = configuration.getElasticSearchMultiSearchEndpoint();
        this.relatedItemSearchBuilder = builder;
        this.configuration = configuration;
        this.getRepository = getRepository;
        String indexNameAlias = configuration.getStorageIndexNameAlias();
        if(indexNameAlias==null || indexNameAlias.trim().length()==0) {
            indexName = configuration.getStorageIndexNamePrefix()+"*";
        } else {
            indexName = indexNameAlias;
        }
        this.typeName = configuration.getStorageContentTypeName();

        StringBuilder b = new StringBuilder(23+typeName.length()+indexName.length());
        b.append("{\"index\":\"").append(indexName).append("\",\"type\":\"").append(typeName).append("\"}\n");
        msearchHeader = b.toString();
    }

    public HttpResult executeSearch(RelatedItemSearch[] searches,HttpElasticClient httpElasticClient) {
        StringBuilder b = new StringBuilder(searches.length*220);

        for(RelatedItemSearch search : searches) {
            b.append(msearchHeader);
            if(search.getRelatedItemSearchType() == RelatedItemSearchType.FREQUENTLY_RELATED_WITH) {
                b.append(createFrequentlyRelatedContentSearch(search)).append('\n');
            }
        }
        String query = b.toString();
        log.debug("executing search {} request(s): {}",searches.length, query);
        return httpElasticClient.executeSearch(HttpMethod.POST,msearchEndpoint,query);
    }

    public SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]>[] processMultiSearchResponse(RelatedItemSearch[] searches,
                                                                                                               HttpResult searchResponse) {

        SearchResultEventWithSearchRequestKey[] results = new SearchResultEventWithSearchRequestKey[searches.length];
        log.debug("parsing json response: {}",searchResponse.getResult());
        FrequentlyRelatedItemSearchResponse[] parsedSearchResults = responseParser.parse(searchResponse.getResult());

//        for(FrequentlyRelatedItemSearchResponse result : parsedSearchResults) {
        for(int i=0;i<searches.length;i++) {
            SearchRequestLookupKey key = searches[i].getLookupKey();
            if(i<parsedSearchResults.length) {
                results[i] = frequentlyRelatedWithResultsConverter(key,parsedSearchResults[i],searches[i].getStartOfRequestNanos());
            } else {
                results[i] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key,-1,searches[i].getStartOfRequestNanos());
            }
        }

        return results;
    }

    private SearchResultEventWithSearchRequestKey<FrequentlyRelatedSearchResult[]> frequentlyRelatedWithResultsConverter(SearchRequestLookupKey key,
                                                                     FrequentlyRelatedItemSearchResponse searchResponse,
                                                                     long requestStartTime) {
        final FrequentlyRelatedSearchResult[] results;
        SearchResultsOutcome outcome;
        if(searchResponse.hasErrored()) {
            log.error("Search response failure for search request key : {}",key,searchResponse.getErrorMessage());
            if(searchResponse!=null) {
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key,searchResponse.getTimeTaken(),requestStartTime);
            } else {
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,key,-1,requestStartTime);
            }
        }
        else {

            int noOfFacets = searchResponse.getNumberOfFacets();
            String[] ids = new String[noOfFacets];
            long[] counts = new long[noOfFacets];
            if(noOfFacets!=0) {
                results = new FrequentlyRelatedSearchResult[noOfFacets];

                while(noOfFacets--!=0) {
                    TermFacet entry = searchResponse.getFacetResult(noOfFacets);
                    ids[noOfFacets] = entry.name;
                    counts[noOfFacets] = entry.count;
                }
                outcome = SearchResultsOutcome.HAS_RESULTS;

                Map<String,String> docs = getRepository.getRelatedItemDocument(ids);

                for(int i=0;i<ids.length;i++) {
                    results[i] = new FrequentlyRelatedSearchResult(ids[i],counts[i],docs.get(ids[i]));
                }
            } else {
                log.debug("no related content found for search key {}",key);
                return new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS,key,searchResponse.getTimeTaken(),requestStartTime);
            }

        }

        return new SearchResultEventWithSearchRequestKey(new SearchResultsEvent(outcome,results),
                                                         key,searchResponse.getTimeTaken(),requestStartTime);

    }


    private String createFrequentlyRelatedContentSearch(RelatedItemSearch search) {
        SearchSourceBuilder builder = relatedItemSearchBuilder.createFrequentlyRelatedContentSearch(search);
        try {
            XContentBuilder xbuilder = XContentFactory.contentBuilder(XContentType.JSON);
            builder.toXContent(xbuilder, ToXContent.EMPTY_PARAMS);
            return xbuilder.string();
        } catch (Exception e) {
            return "{ \"error\" : \"" + e.getMessage() + "\"}";
        }
    }
}
