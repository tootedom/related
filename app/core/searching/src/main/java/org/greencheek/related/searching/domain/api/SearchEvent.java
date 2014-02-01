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

import com.lmax.disruptor.EventFactory;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

/**
 * Respresents either a Search Event, for which the response context requires storing for
 * later processing.  Or a Search Response event for which the response context requires retrieval for
 * for processing the search results.
 */
public class SearchEvent {
    private SearchEventType eventType;
    private SearchRequestLookupKey searchRequest;
    private SearchResponseContext[] responseContexts;
    private SearchResultEventWithSearchRequestKey[] searchResponse;

    public final static EventFactory<SearchEvent> FACTORY = new EventFactory<SearchEvent>()
    {
        @Override
        public SearchEvent newInstance()
        {
            return new SearchEvent();
        }
    };


    public void setEventType(SearchEventType eventType) {
        this.eventType = eventType;
    }

    public void setSearchRequest(SearchRequestLookupKey searchRequest, SearchResponseContext[] holder) {
        this.searchRequest = searchRequest;
        this.responseContexts = holder;
    }

    public void setSearchResponse(SearchResultEventWithSearchRequestKey[] searchResponse) {
        this.searchResponse = searchResponse;
    }

    public SearchEventType getEventType() {
        return eventType;
    }

    public SearchRequestLookupKey getSearchRequest() {
        return searchRequest;
    }

    public SearchResultEventWithSearchRequestKey[] getSearchResponse() {
        return searchResponse;
    }

    public SearchResponseContext[] getResponseContexts() {
        return responseContexts;
    }
}
