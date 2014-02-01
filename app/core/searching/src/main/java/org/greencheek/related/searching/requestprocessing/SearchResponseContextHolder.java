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

/**
 * Holds references to an array of SearchResponseContext objects.
 * There could be multiple SearchResponseContext objects, as the result of a search
 * could be multiplexed (broadcast like a topic in an mq) to multiple places ie:
 *
 * - The user waiting for the search result
 * - Backend process logging search results
 *
 *
 * This class is not thread safe, and should only be used in a thread safe manner (i.e.
 * accessible within the confinds of the disruptor ring buffer)
 */
public class SearchResponseContextHolder {

    private static SearchResponseContext[] EMPTY_RESPONSES = new SearchResponseContext[0];
    private SearchResponseContext[] contexts;

    public SearchResponseContextHolder() {
    }


    public void setContexts(SearchResponseContext... contexts) {
        if(contexts==null) {
            contexts = EMPTY_RESPONSES;
        }
        this.contexts = contexts;
    }

    public SearchResponseContext[] getContexts() {
        return contexts;
    }
}
