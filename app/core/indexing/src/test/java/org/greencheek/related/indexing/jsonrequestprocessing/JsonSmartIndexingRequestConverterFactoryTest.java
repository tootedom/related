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

package org.greencheek.related.indexing.jsonrequestprocessing;

import org.greencheek.related.indexing.IndexingRequestConverter;
import org.greencheek.related.indexing.InvalidIndexingRequestException;
import org.greencheek.related.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/12/2013
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public class JsonSmartIndexingRequestConverterFactoryTest {

    @Test
    public void testJsonSmartConverterIsCreated()
    {
        JsonSmartIndexingRequestConverterFactory factory = new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());

        try {
            factory.createConverter(new SystemPropertiesConfiguration(), ByteBuffer.wrap(new byte[0]));
            fail("Should not be able to create a converter that deals with no data");
        } catch(InvalidIndexingRequestException e) {

        }

        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"items\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";


        IndexingRequestConverter converter = factory.createConverter(new SystemPropertiesConfiguration(), ByteBuffer.wrap(json.getBytes()));

        assertTrue(converter instanceof JsonSmartIndexingRequestConverter);
    }
}
