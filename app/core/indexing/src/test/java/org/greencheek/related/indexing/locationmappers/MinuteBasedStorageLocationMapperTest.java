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

package org.greencheek.related.indexing.locationmappers;

import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateAndHourAndMinuteFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class MinuteBasedStorageLocationMapperTest {

    Configuration config;
    RelatedItemStorageLocationMapper minBasedMapper;

    @Before
    public void setUp() {
        config = new SystemPropertiesConfiguration();
        minBasedMapper = new MinuteBasedStorageLocationMapper(config,new JodaUTCCurrentDateAndHourAndMinuteFormatter());
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_INDEXNAME_DATE_CACHING_ENABLED);
        System.clearProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE);
    }

    @Test
    public void testEmptyDateReturnsToday() {
        RelatedItem product = new RelatedItem("1".toCharArray(),null,null,new RelatedItemAdditionalProperties());
        String name = minBasedMapper.getLocationName(product);


        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd_HH':'mm");
        assertEquals(config.getStorageIndexNamePrefix() + "-" + today.format(new Date()),name);
    }

    @Test
    public void testSetDateReturnsIndexNameWithGivenDate() {
        Date now = new Date();
        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat todayDate = new SimpleDateFormat("yyyy-MM-dd'_'HH':'mm");

        RelatedItem product = new RelatedItem("1".toCharArray(),today.format(now),null,new RelatedItemAdditionalProperties());
        try {
            Thread.sleep(2000);
        } catch(Exception e) {

        }
        RelatedItem product2 = new RelatedItem("1".toCharArray(),today.format(now),null,new RelatedItemAdditionalProperties());
        String name = minBasedMapper.getLocationName(product);
        String name2 = minBasedMapper.getLocationName(product2);


        assertEquals(config.getStorageIndexNamePrefix() + "-" + todayDate.format(now), name);
        assertEquals(config.getStorageIndexNamePrefix() + "-" + todayDate.format(now),name2);

        RelatedItem product3 = new RelatedItem("1".toCharArray(),"1920-01-02T23:59:59+00:00",null,new RelatedItemAdditionalProperties());


        name = minBasedMapper.getLocationName(product3);
        assertEquals(config.getStorageIndexNamePrefix() + "-1920-01-02_23:59",name);
    }

    @Test
    public void testTimeZoneDate() {
        RelatedItem product = new RelatedItem("1".toCharArray(),"1920-01-02T01:59:59+02:00",null,new RelatedItemAdditionalProperties());

        String name = minBasedMapper.getLocationName(product);
        assertEquals(config.getStorageIndexNamePrefix() + "-1920-01-01_23:59",name);
    }

}
