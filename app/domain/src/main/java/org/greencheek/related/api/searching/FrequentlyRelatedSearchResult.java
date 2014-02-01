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
 * Represents a results that contains the frequency of the id occurring.
 */
public class FrequentlyRelatedSearchResult {



    private final long frequency;
    private final String relatedItemId;

    public FrequentlyRelatedSearchResult(String id, long frequency) {
        this.frequency = frequency;
        this.relatedItemId = id;
    }

    public String getRelatedItemId() {
        return relatedItemId;
    }


    public long getFrequency() {
        return frequency;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(32);
        b.append(frequency).append(':').append(relatedItemId);
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrequentlyRelatedSearchResult that = (FrequentlyRelatedSearchResult) o;
        if (frequency != that.frequency) return false;
        if (relatedItemId != null ? !relatedItemId.equals(that.relatedItemId) : that.relatedItemId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (frequency ^ (frequency >>> 32));
        result = 31 * result + (relatedItemId != null ? relatedItemId.hashCode() : 0);
        return result;
    }

}
