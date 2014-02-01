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

package org.greencheek.related.api.indexing;


import org.greencheek.related.api.RelatedItemAdditionalProperties;


public class RelatedItem {

    private final String date;
    private final char[] id;

    private final char[][] relatedItemIds;
    private final RelatedItemAdditionalProperties additionalProperties;


    public RelatedItem(char[] id, String date, char[][] relatedPids, RelatedItemAdditionalProperties properties) {
        this.id = id;
        this.date = date;
        this.relatedItemIds = relatedPids;
        this.additionalProperties = properties;
    }

    public String getDate() {
        return date;
    }

    public char[] getId() {
        return id;
    }

    public char[][] getRelatedItemPids() {
        return relatedItemIds;
    }

    public RelatedItemAdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }


}
