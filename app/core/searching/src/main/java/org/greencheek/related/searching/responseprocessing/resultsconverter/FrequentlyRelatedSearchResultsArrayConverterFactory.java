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

package org.greencheek.related.searching.responseprocessing.resultsconverter;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 29/06/2013
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedSearchResultsArrayConverterFactory implements SearchResultsConverterFactory {

    private final SearchResultsConverter frequentlyRelated;


    public FrequentlyRelatedSearchResultsArrayConverterFactory(SearchResultsConverter<FrequentlyRelatedSearchResult[]> frequentlyRelated) {
        this.frequentlyRelated = frequentlyRelated;
    }

    @Override
    public <T> SearchResultsConverter<T> getConverter(Class<T> searchType) {
        if(searchType == FrequentlyRelatedSearchResult[].class) {
            return frequentlyRelated;
        } else {
            return null;
        }
    }
}
