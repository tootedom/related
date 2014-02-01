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
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class DayBasedStorageLocationMapperTest {

    Configuration cachingDayConfig;
    Configuration noncachingDayConfig;
    RelatedItemStorageLocationMapper cachingDayBasedMapper;
    RelatedItemStorageLocationMapper nonCachingDayBasedMapper;

    @Before
    public void setUp() {
        System.setProperty(ConfigurationConstants.PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,"true");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE,"10");
        cachingDayConfig = new SystemPropertiesConfiguration();
        cachingDayBasedMapper = new DayBasedStorageLocationMapper(cachingDayConfig,new JodaUTCCurrentDateFormatter());

        System.setProperty(ConfigurationConstants.PROPNAME_INDEXNAME_DATE_CACHING_ENABLED,"false");
        noncachingDayConfig = new SystemPropertiesConfiguration();
        nonCachingDayBasedMapper = new DayBasedStorageLocationMapper(noncachingDayConfig,new JodaUTCCurrentDateFormatter());
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_INDEXNAME_DATE_CACHING_ENABLED);
        System.clearProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXNAMES_TO_CACHE);
    }

    @Test
    public void testEmptyDateReturnsToday() {
        RelatedItem product = new RelatedItem("1".toCharArray(),null,null,new RelatedItemAdditionalProperties());
        String name = cachingDayBasedMapper.getLocationName(product);


        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(cachingDayConfig.getStorageIndexNamePrefix() + "-" + today.format(new Date()),name);
    }

    @Test
    public void testSetDateReturnsIndexNameWithGivenDate() {
        Date now = new Date();
        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat todayDate = new SimpleDateFormat("yyyy-MM-dd");

        RelatedItem product = new RelatedItem("1".toCharArray(),today.format(now),null,new RelatedItemAdditionalProperties());
        try {
            Thread.sleep(2000);
        } catch(Exception e) {

        }
        RelatedItem product2 = new RelatedItem("1".toCharArray(),today.format(now),null,new RelatedItemAdditionalProperties());
        String name = cachingDayBasedMapper.getLocationName(product);
        String name2 = cachingDayBasedMapper.getLocationName(product2);


        assertEquals(cachingDayConfig.getStorageIndexNamePrefix() + "-" + todayDate.format(new Date()),name);
        assertEquals(cachingDayConfig.getStorageIndexNamePrefix() + "-" + todayDate.format(new Date()),name2);


        product = new RelatedItem("1".toCharArray(),today.format(now),null,new RelatedItemAdditionalProperties());
        try {
            Thread.sleep(2000);
        } catch(Exception e) {

        }
        product2 = new RelatedItem("1".toCharArray(),today.format(now),null,new RelatedItemAdditionalProperties());
        name = nonCachingDayBasedMapper.getLocationName(product);
        name2 = nonCachingDayBasedMapper.getLocationName(product2);


        assertEquals(noncachingDayConfig.getStorageIndexNamePrefix() + "-" + todayDate.format(new Date()),name);
        assertEquals(noncachingDayConfig.getStorageIndexNamePrefix() + "-" + todayDate.format(new Date()),name2);



        RelatedItem product3 = new RelatedItem("1".toCharArray(),"1920-01-02T23:59:59+00:00",null,new RelatedItemAdditionalProperties());


        name = nonCachingDayBasedMapper.getLocationName(product3);
        assertEquals(noncachingDayConfig.getStorageIndexNamePrefix() + "-1920-01-02",name);
    }

    @Test
    public void testTimeZoneDate() {
        RelatedItem product = new RelatedItem("1".toCharArray(),"1920-01-02T01:59:59+02:00",null,new RelatedItemAdditionalProperties());


        String name = nonCachingDayBasedMapper.getLocationName(product);
        assertEquals(noncachingDayConfig.getStorageIndexNamePrefix() + "-1920-01-01",name);
    }

    @Test
    public void testCacheDoesNotGrowOverMaxCached() {
        RelatedItem product1 = new RelatedItem("1".toCharArray(),"1920-01-03T01:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product2 = new RelatedItem("1".toCharArray(),"1920-01-04T02:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product3 = new RelatedItem("1".toCharArray(),"1920-01-05T03:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product4 = new RelatedItem("1".toCharArray(),"1920-01-06T04:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product5 = new RelatedItem("1".toCharArray(),"1920-01-07T05:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product6 = new RelatedItem("1".toCharArray(),"1920-01-08T06:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product7 = new RelatedItem("1".toCharArray(),"1920-01-09T07:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product8 = new RelatedItem("1".toCharArray(),"1920-01-10T01:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product9 = new RelatedItem("1".toCharArray(),"1920-01-11T01:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product10 = new RelatedItem("1".toCharArray(),"1920-01-12T01:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product11 = new RelatedItem("1".toCharArray(),"1920-01-13T01:59:59+00:00",null,new RelatedItemAdditionalProperties());
        RelatedItem product12 = new RelatedItem("1".toCharArray(),"1920-01-14T01:59:59+00:00",null,new RelatedItemAdditionalProperties());



        String name = cachingDayBasedMapper.getLocationName(product1);
        name = cachingDayBasedMapper.getLocationName(product2);
        name = cachingDayBasedMapper.getLocationName(product3);
        name = cachingDayBasedMapper.getLocationName(product4);
        name = cachingDayBasedMapper.getLocationName(product5);
        name = cachingDayBasedMapper.getLocationName(product6);
        name = cachingDayBasedMapper.getLocationName(product7);
        name = cachingDayBasedMapper.getLocationName(product8);
        name = cachingDayBasedMapper.getLocationName(product9);
        name = cachingDayBasedMapper.getLocationName(product10);
        name = cachingDayBasedMapper.getLocationName(product11);
        name = cachingDayBasedMapper.getLocationName(product12);

        try {
            Field cache = null;
            cache = DayBasedStorageLocationMapper.class.getDeclaredField("dayCache");
            cache.setAccessible(true);
            ConcurrentMap m  = (ConcurrentMap) cache.get(cachingDayBasedMapper);
            assertEquals(10, m.size());
        } catch (NoSuchFieldException e) {
            fail();
        } catch (IllegalAccessException e) {
            fail();
        }


        assertEquals(cachingDayConfig.getStorageIndexNamePrefix() + "-1920-01-14",name);
    }
}
