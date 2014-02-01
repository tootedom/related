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

import org.greencheek.related.util.config.Configuration;

import java.util.Map;

/**
 * verifies the presence of the field {@link org.greencheek.related.util.config.Configuration#getRequestParameterForId()}
 * within the given requests parameters provided by the user.
 */
public class FrequentlyRelatedContentRequestParameterValidator implements SearchRequestParameterValidator {

    public final ValidationMessage VALID_ID_MESSAGE;
    public final ValidationMessage INVALID_ID_MESSAGE;
    private final String idParameter;

    public FrequentlyRelatedContentRequestParameterValidator(Configuration configuration) {
        this.idParameter = configuration.getRequestParameterForId();

        VALID_ID_MESSAGE = new ValidationMessage(true,idParameter,"");
        INVALID_ID_MESSAGE = new ValidationMessage(false,idParameter,"no id present in parameters");
    }

    @Override
    public ValidationMessage validateParameters(Map<String, String> requestParameters) {
        String id = requestParameters.get(idParameter);
        if(id == null || id.length()==0) return INVALID_ID_MESSAGE;
        else return VALID_ID_MESSAGE;
    }
}
