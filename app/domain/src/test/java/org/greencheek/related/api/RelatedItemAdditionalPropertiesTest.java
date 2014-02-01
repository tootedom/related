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
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 06/10/2013
 * Time: 20:31
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemAdditionalPropertiesTest {


    private Configuration config;

    @Before
    public void setup() {
        config = new SystemPropertiesConfiguration();
    }

    @Test
    public void testPropertyMerging() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,2);
        RelatedItemAdditionalProperties properties2 = new RelatedItemAdditionalProperties(config,2);

        properties1.addProperty("name", "value");
        properties1.addProperty("type", "socks");

        properties2.addProperty("site", "amazon");

        RelatedItemAdditionalProperties combined = new RelatedItemAdditionalProperties(properties1,properties2);

        assertEquals("Should have 3 properties in combined properties",3,combined.getNumberOfProperties());

        assertEquals("Property 1 name should be 'name'","name",combined.getPropertyName(0));
        assertEquals("Property 1 value should be 'value'","value",combined.getPropertyValue(0));

        assertEquals("Property 2 name should be 'type'","type",combined.getPropertyName(1));
        assertEquals("Property 2 name should be 'socks'","socks",combined.getPropertyValue(1));

        assertEquals("Property 3 name should be 'site'","site",combined.getPropertyName(2));
        assertEquals("Property 3 name should be 'amazon'","amazon",combined.getPropertyValue(2));


    }

    @Test
    public void testPropertySetting() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,2);

        properties1.addProperty("name","value");
        properties1.addProperty("type","socks");

        assertEquals("Should have 2 properties in combined properties",2,properties1.getNumberOfProperties());

        assertEquals("Property 1 name should be 'name'","name",properties1.getPropertyName(0));
        assertEquals("Property 1 value should be 'value'","value",properties1.getPropertyValue(0));

        assertEquals("Property 2 name should be 'type'","type",properties1.getPropertyName(1));
        assertEquals("Property 2 name should be 'socks'","socks",properties1.getPropertyValue(1));

        properties1.setProperty("channel","uk",0);

        assertEquals("Property 1 name should be 'channel'","channel",properties1.getPropertyName(0));
        assertEquals("Property 1 value should be 'uk'","uk",properties1.getPropertyValue(0));

    }

    @Test
    public void testPropertyStringLength() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,3);

        properties1.addProperty("name","value");
        properties1.addProperty("type","socks");
        properties1.addProperty("site","amazon");

        assertEquals("Should have 3 properties in combined properties",3,properties1.getNumberOfProperties());

        assertEquals("Should have a string length of: 33", 33, properties1.getUrlQueryTypeStringLength());
    }

    @Test
    public void testPropertiesToString() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,3);

        properties1.addProperty("name","value");
        properties1.addProperty("type","socks");
        properties1.addProperty("site","amazon");

        assertEquals("Properties object should be represented as: 'name=value&type=socks&site=amazon'","name=value&type=socks&site=amazon",properties1.toUrlQueryTypeString());

        properties1 = new RelatedItemAdditionalProperties(config,3);


        assertEquals("Properties object should be represented as an empty string: ''","",properties1.toUrlQueryTypeString());
    }

    @Test
    public void testCopyProperties() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,3);

        properties1.addProperty("name","value");
        properties1.addProperty("type","socks");

        RelatedItemAdditionalProperties properties2 = new RelatedItemAdditionalProperties(config,2);

        properties2.addProperty("channel","uk");

        assertEquals("Properties object should have 1 property",1,properties2.getNumberOfProperties());
        properties1.copyTo(properties2);

        assertEquals("Properties object should have 2 property",2,properties2.getNumberOfProperties());


        assertEquals("Property 1 name should be 'name'","name",properties2.getPropertyName(0));
        assertEquals("Property 1 value should be 'value'","value",properties2.getPropertyValue(0));

        assertEquals("Property 2 name should be 'type'","type",properties2.getPropertyName(1));
        assertEquals("Property 2 name should be 'socks'","socks",properties2.getPropertyValue(1));


    }

    @Test
    public void testConvertToArray() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,3);

        properties1.addProperty("name","value");
        properties1.addProperty("type","socks");

        String[][] props = properties1.convertToStringArray();

        assertEquals("Should have a two dimensional array with 2 rows", 2,props.length);

        String[] prop1 = props[0];

        assertEquals("Should have a two dimensional array with 2 columns", 2,prop1.length);
        assertEquals("The first element in the array should be the name","name",prop1[0]);
        assertEquals("The 2nd element in the array should be the value","value",prop1[1]);

        String[] prop2 = props[1];

        assertEquals("Should have a two dimensional array with 2 columns", 2,prop2.length);
        assertEquals("The first element in the array should be the name 'type'","type",prop2[0]);
        assertEquals("The 2nd element in the array should be the value","socks",prop2[1]);
    }

    @Test
    public void testConvertToMap() {
        RelatedItemAdditionalProperties properties1 = new RelatedItemAdditionalProperties(config,3);

        properties1.addProperty("name","value");
        properties1.addProperty("type","socks");

        Map<String,String> props = new HashMap<String,String>();
        properties1.convertTo(props);

        assertEquals("Map shoudl have 2 entries",2,props.size());
        assertTrue("Map should have 'name' entry",props.containsKey("name"));
        assertTrue("Map should have 'type' entry",props.containsKey("type"));

        assertEquals("Map's value for 'name' key should be 'value","value",props.get("name"));
        assertEquals("Map's value for 'type' key should be 'socks","socks",props.get("type"));

    }
}
