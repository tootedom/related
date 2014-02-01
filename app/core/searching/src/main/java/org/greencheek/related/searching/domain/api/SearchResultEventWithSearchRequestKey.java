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

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;

/**
 * A {@link SearchResultsEvent} associated with the {@link org.greencheek.related.api.searching.lookup.SearchRequestLookupKey}
 * that was responsible for that search result
 */
public class SearchResultEventWithSearchRequestKey<T> {
    private final SearchResultsEvent<T> result;
    private final SearchRequestLookupKey request;
    private final long searchExecutionTimeInMillis;
    private final long startOfSearchRequestProcessing;

    public SearchResultEventWithSearchRequestKey(SearchResultsEvent<T> result, SearchRequestLookupKey requestKey,
                                                 long searchExecutionTimeInMillis,long startOfSearchRequestProcessing) {
        this.request = requestKey;
        this.result = result;
        this.searchExecutionTimeInMillis = searchExecutionTimeInMillis;
        this.startOfSearchRequestProcessing = startOfSearchRequestProcessing;
    }


    public SearchRequestLookupKey getRequest() {
        return request;
    }

    public SearchResultsEvent<T> getResponse() {
        return result;
    }

    public long getSearchExecutionTime() {
        return searchExecutionTimeInMillis;
    }

    public long getStartOfSearchRequestProcessing() {
        return startOfSearchRequestProcessing;
    }
}
