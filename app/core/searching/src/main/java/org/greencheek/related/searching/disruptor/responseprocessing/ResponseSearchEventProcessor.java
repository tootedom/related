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

package org.greencheek.related.searching.disruptor.responseprocessing;


import org.greencheek.related.searching.domain.api.SearchEvent;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Processes SearchEvents for which {@link org.greencheek.related.searching.domain.api.SearchEvent#getEventType()}
 * returns {@link org.greencheek.related.searching.domain.api.SearchEventType#RESPONSE}
 *
 * Given a SearchEvent obtains the associated array of {@link SearchResultEventWithSearchRequestKey} objects
 * obtaining the response contexts associated with search request ket for which the search was performed,
 * and passes these and the search result to the {@link org.greencheek.related.searching.disruptor.responseprocessing.ResponseEventHandler}
 * for processing
 */
public class ResponseSearchEventProcessor implements SearchEventProcessor {
    private static final Logger log = LoggerFactory.getLogger(ResponseSearchEventProcessor.class);

    private final SearchResponseContextLookup searchContext;
    private final ResponseEventHandler responseEventHandler;

    public ResponseSearchEventProcessor(SearchResponseContextLookup searchContext,
                                        ResponseEventHandler responseHandler) {
        this.searchContext = searchContext;
        this.responseEventHandler = responseHandler;

    }

    @Override
    public void processSearchEvent(SearchEvent event) {
        SearchResultEventWithSearchRequestKey[] results = event.getSearchResponse();
        log.debug("Distributing {} search response(s) to awaiting parties",results.length);

        List<List<SearchResponseContext>> responseContexts = new ArrayList<List<SearchResponseContext>>(results.length);
//        SearchResultsEvent[] searchResults = new SearchResultsEvent[results.length];

        for (int i = 0; i < results.length; i++) {
            SearchResultEventWithSearchRequestKey res = results[i];
            responseContexts.add(searchContext.removeContexts(res.getRequest()));
//            searchResults[i] = res.getResponse();
        }

        responseEventHandler.handleResponseEvents(results,responseContexts);
    }

    @Override
    public void shutdown() {
        log.debug("Stopping the response event handler");
        this.responseEventHandler.shutdown();
    }
}
