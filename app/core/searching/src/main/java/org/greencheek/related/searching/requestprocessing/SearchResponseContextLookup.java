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

package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;

import java.util.List;

/**
 * Represents a multi-map type lookup service that stores user search requests,
 * against the response context.  The response context allows us to obtain the
 * response based object through which we can send the results of the user search.
 *
 * There will only be one thread calling this data structure.  It will either be removing Contexts
 * or adding them.
 */
public interface SearchResponseContextLookup {

    /**
     * Removes the context holder.
     *
     * @param key the search key, which represents the user's search request
     * @return
     */
    public List<SearchResponseContext> removeContexts(SearchRequestLookupKey key);

    /**
     * adds the given SearchResponseContextHolder against the given key.  If the key already exists,
     * then the context is added to that pre-existing list of contexts for that key (the key represents
     * a search request)
     *
     * @param key the search key representing  a user search request
     * @param context the search context to add.
     */
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] context);
}
