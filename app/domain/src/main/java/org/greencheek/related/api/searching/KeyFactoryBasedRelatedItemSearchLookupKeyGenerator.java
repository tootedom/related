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

import org.greencheek.related.api.searching.lookup.RelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class KeyFactoryBasedRelatedItemSearchLookupKeyGenerator implements RelatedItemSearchLookupKeyGenerator {

    private final SearchRequestLookupKeyFactory keyFactory;
    private final Configuration configuration;

    public KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(Configuration configuration, SearchRequestLookupKeyFactory factory) {
        this.configuration = configuration;
        this.keyFactory = factory;
    }

    @Override
    public SearchRequestLookupKey createSearchRequestLookupKeyFor(RelatedItemSearch userSearch) {
        return createLookupKey(configuration,userSearch);
    }

    @Override
    public void setSearchRequestLookupKeyOn(RelatedItemSearch userSearch) {
        userSearch.setLookupKey(createLookupKey(configuration,userSearch));
    }

    private int getStringLength(Configuration configuration, RelatedItemSearch search) {
        return search.getAdditionalSearchCriteria().getUrlQueryTypeStringLength() + configuration.getRelatedItemIdLength() + RelatedItemSearch.RESULTS_SET_SIZE_KEY_LENGTH +
               RelatedItemSearch.ID_KEY_LENGTH + 4;

    }

    private SearchRequestLookupKey createLookupKey(Configuration configuration, RelatedItemSearch search) {
        StringBuilder string = new StringBuilder(getStringLength(configuration,search));
        string.append(RelatedItemSearch.ID_KEY).append('=').append(search.getRelatedItemId()).append('&');
        string.append(RelatedItemSearch.RESULTS_SET_SIZE_KEY).append('=').append(search.getMaxResults()).append('&');
        string.append(search.getAdditionalSearchCriteria().toUrlQueryTypeString());
        return keyFactory.createSearchRequestLookupKey(string.toString());
    }

}
