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

package org.greencheek.related.api.searching.lookup;

import org.greencheek.related.api.searching.RelatedItemSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedItemSearchLookupKeyGenerator {

    /**
     * Given a RelatedItemSearch object, a SearchRequestLookup key is generated.
     * the generated key is created from the properties that have been set on that RelatedItemSearch
     * object.
     *
     * @param userSearch The user search.
     * @return the SearchRequestLookupKey that represents that userSearch.
     */
    SearchRequestLookupKey createSearchRequestLookupKeyFor(RelatedItemSearch userSearch);


    /**
     * Given a RelatedItemSearch object, a SearchRequestLookup key is generated.
     * the generated key is created from the properties that have been set on that RelatedItemSearch
     * object.
     *
     * This method has the side effect of modifying the RelatedItemSearch object to set it's lookupKey (
     * @see org.greencheek.related.api.searching.RelatedItemSearch#setLookupKey(org.greencheek.related.api.searching.lookup.SearchRequestLookupKey)
     *
     * @param userSearch The user search.
     * @return the SearchRequestLookupKey that represents that userSearch.
     */
    void setSearchRequestLookupKeyOn(RelatedItemSearch userSearch);
}
