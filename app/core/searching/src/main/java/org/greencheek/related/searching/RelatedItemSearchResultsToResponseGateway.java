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

package org.greencheek.related.searching;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

/**
 * A go between for storing against a search request key a number of awaiting response objects.
 * These response object(s) are later retrieved, via the search request key, in order to send search
 * results to those awaiting response objects.  The response object naturally represents some means of
 * being able to send results to a location that is awaiting the search results.
 */
public interface RelatedItemSearchResultsToResponseGateway {

    /**
     * Associates with a certain search request, a ResponseContext.  The ResponseContext(s) can later be
     * retrieved in order to send search results to waiting parties
     */
    public void storeResponseContextForSearchRequest(SearchRequestLookupKey key, SearchResponseContext[] context);

    /**
     * A search has completed and needs to be sent to the stored response processors
     * @param multipleSearchResults
     */
    public void sendSearchResultsToResponseContexts(SearchResultEventWithSearchRequestKey[] multipleSearchResults);

    /**
     * shutdown the response gateway
     */
    public void shutdown();
}
