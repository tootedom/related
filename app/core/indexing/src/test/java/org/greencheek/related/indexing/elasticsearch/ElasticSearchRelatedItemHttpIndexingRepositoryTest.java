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

package org.greencheek.related.indexing.elasticsearch;


import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.elastic.http.ahc.AHCHttpElasticSearchClientFactory;
import org.greencheek.related.elastic.http.ahc.AHCHttpSniffAvailableNodes;
import org.greencheek.related.elastic.util.ElasticSearchServer;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.related.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.related.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;

import org.junit.*;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 17/06/2013
 * Time: 09:29
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedItemHttpIndexingRepositoryTest {

    private RelatedItemStorageRepository repository;
    private static Configuration configuration;
    private static UTCCurrentDateFormatter dateFormatter;
    private static RelatedItemStorageLocationMapper dayStorageLocationMapper;
    private ElasticSearchServer server;
    private HttpElasticSearchClientFactory factory;

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_INDEX_NAME_PREFIX);
        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_ES_CLIENT_TYPE);
        if(server!=null) {
            server.shutdown();
        }

        repository.shutdown();

    }

    @Before
    public void setUp() {
        // Instantiates a local node & client
        configuration = new SystemPropertiesConfiguration();
        // Clean all data

        dateFormatter = new JodaUTCCurrentDateFormatter();

        dayStorageLocationMapper = new DayBasedStorageLocationMapper(configuration,dateFormatter);

        String indexName = "relatedprog";
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_INDEX_NAME_PREFIX,indexName);
        // Set the clustername
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME, "relatedprogrammes");
        configuration = new SystemPropertiesConfiguration();

        // Start the Elastic Search Server
        server = new ElasticSearchServer(configuration.getStorageClusterName(),false,true);

        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS,"http://localhost:"+server.getHttpPort());
        System.setProperty(ConfigurationConstants.PROPNAME_ES_CLIENT_TYPE,"http");

        if(!server.isSetup()) throw new RuntimeException("ElasticSearch Not set");

        server.setIndexTemplate(indexName);

        // Create the client pointing to the above server
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,"localhost:" + server.getPort());
        configuration = new SystemPropertiesConfiguration();

        // Create the repo
        factory = new AHCHttpElasticSearchClientFactory(configuration);

        repository = new ElasticSearchRelatedItemStorageRepositoryFactory(configuration,factory).getRepository(configuration);


    }

    private RelatedItem createRelatedItemWithCurrentDay() {
        return createRelatedItemWithGivenDate(null);
    }

    private RelatedItem createRelatedItemWithGivenDate(String date) {
        return createRelatedItemWithGivenDateAndProperties((date != null) ? date : dateFormatter.getCurrentDay(), null);
    }

    private RelatedItem createRelatedItemWithGivenDateAndProperties(String date, Map<String, String> customProps) {
        if(customProps==null) {
            return new RelatedItem(UUID.randomUUID().toString().toCharArray(),date,new char[][]{UUID.randomUUID().toString().toCharArray()},new RelatedItemAdditionalProperties(configuration,0));
        } else {
            return new RelatedItem(UUID.randomUUID().toString().toCharArray(),date,new char[][]{UUID.randomUUID().toString().toCharArray()}, convertFrom(configuration, customProps));
        }
    }

    @Test
    public void testSingleStoreOfProductWithTodaysDate() throws Exception {
        RelatedItem productToStore = createRelatedItemWithCurrentDay();
        indexAndFlush(Arrays.asList(productToStore));
        int count = server.getDocCount(dayStorageLocationMapper.getLocationName(productToStore));
        assertEquals(1,count);
    }

    @Test
    public void testStoreOfMultipleProductsWithTodaysDate() throws Exception {
        RelatedItem productToStore = createRelatedItemWithCurrentDay();
        RelatedItem productToStore2 = createRelatedItemWithCurrentDay();

        indexAndFlush(Arrays.asList(productToStore,productToStore2));
        int count = server.getDocCount(dayStorageLocationMapper.getLocationName(productToStore));
        assertEquals(2,count);
    }

    @Test
    public void testStoreOfMultipleProductsWithTodaysAndOtherDate() throws Exception {
        RelatedItem productToStore1 = createRelatedItemWithCurrentDay();
        RelatedItem productToStore2 = createRelatedItemWithCurrentDay();
        RelatedItem productToStore3 = createRelatedItemWithGivenDate("1978-06-13");
        RelatedItem productToStore4 = createRelatedItemWithGivenDate("1978-06-14");
        RelatedItem productToStore5 = createRelatedItemWithGivenDate("1978-06-14");
        RelatedItem productToStore6 = createRelatedItemWithGivenDate("1978-06-14");

        indexAndFlush(Arrays.asList(productToStore1,productToStore2,productToStore3,
                                    productToStore4,productToStore5,productToStore6));
        int count = server.getDocCount(dayStorageLocationMapper.getLocationName(productToStore1));
        assertEquals(2,count);

        count = server.getDocCount(dayStorageLocationMapper.getLocationName(productToStore3));
        assertEquals(1,count);
        count = server.getDocCount(dayStorageLocationMapper.getLocationName(productToStore4));
        assertEquals(3,count);
    }




    public static RelatedItemAdditionalProperties convertFrom(Configuration config, Map<String,String> propertes) {
        RelatedItemAdditionalProperties propArrays = new RelatedItemAdditionalProperties(config,propertes.size());

        int i=0;
        for(Map.Entry<String,String> entry : propertes.entrySet() ) {
            propArrays.setProperty(entry.getKey(),entry.getValue(),i++);
        }
        propArrays.setNumberOfProperties(propertes.size());
        return propArrays;
    }



    private void indexAndFlush(RelatedItemStorageLocationMapper storageLocationMapper, List<RelatedItem> products) {
        repository.store(storageLocationMapper, products);
        for(RelatedItem product : products) {
            server.flush(storageLocationMapper.getLocationName(product));
        }

    }

    private void indexAndFlush(List<RelatedItem> products) {
       indexAndFlush(dayStorageLocationMapper,products);
    }



    @Test
    public void testShutdown() throws Exception {

    }


}
