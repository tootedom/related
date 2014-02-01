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

import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.util.config.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores validators that can be used to validate request parameters
 */
public class MapBasedSearchRequestParameterValidatorLookup implements SearchRequestParameterValidatorLocator {

    private final Map<RelatedItemSearchType,SearchRequestParameterValidator> validatorMap = new HashMap<RelatedItemSearchType,SearchRequestParameterValidator>(2);

    public MapBasedSearchRequestParameterValidatorLookup(Configuration configuration) {
        validatorMap.put(RelatedItemSearchType.FREQUENTLY_RELATED_WITH,new FrequentlyRelatedContentRequestParameterValidator(configuration));
    }

    @Override
    public SearchRequestParameterValidator getValidatorForType(RelatedItemSearchType type) {
        return validatorMap.get(type);
    }
}
