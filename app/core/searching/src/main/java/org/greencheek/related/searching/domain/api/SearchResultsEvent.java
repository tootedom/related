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

package org.greencheek.related.searching.domain.api;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.SearchResultsOutcome;

/**
 *
 */
public class SearchResultsEvent<T> {

    private final static FrequentlyRelatedSearchResult[] EMPTY_FRSR= new FrequentlyRelatedSearchResult[0];

    public final static SearchResultsEvent<FrequentlyRelatedSearchResult[]> EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent<FrequentlyRelatedSearchResult[]>(SearchResultsOutcome.EMPTY_RESULTS, EMPTY_FRSR);
    public final static SearchResultsEvent<FrequentlyRelatedSearchResult[]> EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent<FrequentlyRelatedSearchResult[]>(SearchResultsOutcome.FAILED_REQUEST,EMPTY_FRSR);
    public final static SearchResultsEvent<FrequentlyRelatedSearchResult[]> EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent<FrequentlyRelatedSearchResult[]>(SearchResultsOutcome.REQUEST_TIMEOUT, EMPTY_FRSR);

    private final SearchResultsOutcome outcomeType;
    private final Class searchResultsType;
    private final T searchResults;

    public SearchResultsEvent(SearchResultsOutcome outcomeType,
                              T results) {
        this.outcomeType = outcomeType;
        this.searchResults = results;
        this.searchResultsType = results.getClass();

    }

    public SearchResultsOutcome getOutcomeType() {
        return this.outcomeType;
    }

    public Class<T> getSearchResultsType() {
        return searchResultsType;
    }

    public T getSearchResults() {
        return searchResults;
    }
}
