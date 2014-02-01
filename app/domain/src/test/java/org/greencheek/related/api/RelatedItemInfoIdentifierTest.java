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

package org.greencheek.related.api;

import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RelatedItemInfoIdentifierTest {

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_RELATED_ITEM_ID_LENGTH);
    }

    @Test
    public void testIdTruncation() {
        System.setProperty(ConfigurationConstants.PROPNAME_RELATED_ITEM_ID_LENGTH,"10");
        Configuration c = new SystemPropertiesConfiguration();
        RelatedItemInfoIdentifier id = new RelatedItemInfoIdentifier(c);

        id.setId("1234567891");
        assertEquals("1234567891",id.toString());

        id.setId("12345678910");
        assertEquals("1234567891",id.toString());
    }

    @Test
    public void testIdDuplication() {
        System.setProperty(ConfigurationConstants.PROPNAME_RELATED_ITEM_ID_LENGTH,"10");
        Configuration c = new SystemPropertiesConfiguration();
        RelatedItemInfoIdentifier id = new RelatedItemInfoIdentifier(c);

        id.setId("12345678910123456789");

        char[] duplicatedChars = id.duplicate();

        assertEquals(10,duplicatedChars.length);
        assertEquals("1234567891",new String(duplicatedChars));

        duplicatedChars[0]  = 'p';
        assertEquals("1234567891",id.toString());

        id.getIdCharArray()[0] = 'p';

        assertEquals("p234567891",id.toString());


    }
}
