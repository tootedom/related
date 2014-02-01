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
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertSame;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedContentRequestParameterValidatorTest {


    private final Configuration configuration = new SystemPropertiesConfiguration();
    private final FrequentlyRelatedContentRequestParameterValidator validator = new FrequentlyRelatedContentRequestParameterValidator(new SystemPropertiesConfiguration());

    @Test
    public void testMissingIdParameterReturnsInvalidMessage() {
        ValidationMessage message = validator.validateParameters(new HashMap<String, String>());

        assertSame(message,validator.INVALID_ID_MESSAGE);
    }

    @Test
    public void testIdParameterReturnsValidMessage() {
        ValidationMessage message = validator.validateParameters(new HashMap<String, String>(){{ put(configuration.getKeyForFrequencyResultId(),"11111");}});

        assertSame(message,validator.VALID_ID_MESSAGE);
    }
}
