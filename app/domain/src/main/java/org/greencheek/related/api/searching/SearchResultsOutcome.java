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

package org.greencheek.related.api.searching;

/**
 * Enum to represent the success or otherwise
 * of the search.
 */
public enum SearchResultsOutcome {
    HAS_RESULTS(0),
    REQUEST_TIMEOUT(1),
    EMPTY_RESULTS(2),
    FAILED_REQUEST(3),
    MISSING_SEARCH_RESULTS_HANDLER(4);

    private final int ordinalIndex;

    private SearchResultsOutcome(int code) {
        this.ordinalIndex = code;
    }

    public int getIndex() {
        return ordinalIndex;
    }
}
