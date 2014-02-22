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

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.elastic.http.HttpElasticClient;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.elastic.http.HttpResult;
import org.greencheek.related.elastic.http.HttpSearchExecutionStatus;
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
public class ElasticSearchRelatedItemHttpSearchRepository implements RelatedItemSearchRepository<FrequentlyRelatedSearchResult[]> {


    private static final Logger log = LoggerFactory.getLogger(ElasticSearchRelatedItemHttpSearchRepository.class);

    private final HttpElasticClient elasticClient;
    private final Configuration configuration;
    private final ElasticSearchFrequentlyRelatedItemHttpSearchProcessor frequentlyRelatedWithSearchBuilder;


    public ElasticSearchRelatedItemHttpSearchRepository(Configuration configuration,
                                                        HttpElasticSearchClientFactory factory,
                                                        ElasticSearchFrequentlyRelatedItemHttpSearchProcessor builder) {
        this.configuration = configuration;
        this.elasticClient = factory.getClient();
        this.frequentlyRelatedWithSearchBuilder = builder;
    }


    @Override
    public SearchResultEventWithSearchRequestKey[] findRelatedItems(Configuration configuration,
                                                                    RelatedItemSearch[] searches) {
        log.debug("request to execute {} searches", searches.length);
        SearchResultEventWithSearchRequestKey[] results;
        HttpResult sr;
        long startNanos = System.nanoTime();

        sr = frequentlyRelatedWithSearchBuilder.executeSearch(searches, elasticClient);
        HttpSearchExecutionStatus searchRequestStatus = sr.getStatus();
        if (searchRequestStatus == HttpSearchExecutionStatus.OK) {
            log.debug("Processing results for search {} request(s)", searches.length);
            results = frequentlyRelatedWithSearchBuilder.processMultiSearchResponse(searches, sr);
            log.debug("Search Completed, returning processed results.");
        } else if (searchRequestStatus == HttpSearchExecutionStatus.REQUEST_FAILURE) {
            long time = (System.nanoTime() - startNanos) / 1000000;

            log.warn("Exception executing search request");
            int size = searches.length;
            results = new SearchResultEventWithSearchRequestKey[size];
            for (int i = 0; i < size; i++) {
                SearchRequestLookupKey key = searches[i].getLookupKey();
                results[i] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS, key, time, searches[i].getStartOfRequestNanos());
            }
        } else {
            long time = (System.nanoTime() - startNanos) / 1000000;
            if (searchRequestStatus == HttpSearchExecutionStatus.REQUEST_TIMEOUT) {
                log.warn("Request timeout executing search request");
            } else {
                log.warn("Connection timeout executing search request");
            }
            int size = searches.length;
            results = new SearchResultEventWithSearchRequestKey[size];

            for (int i = 0; i < size; i++) {
                SearchRequestLookupKey key = searches[i].getLookupKey();
                results[i] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS, key, time, searches[i].getStartOfRequestNanos());
            }
        }


        return results;
    }

    @Override
    public void shutdown() {
        try {
            elasticClient.shutdown();
        } catch(Exception e) {
            log.warn("unable to stop ");
        }
    }

}
