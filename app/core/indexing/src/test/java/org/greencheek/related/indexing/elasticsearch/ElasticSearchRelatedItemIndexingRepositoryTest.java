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

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.util.ElasticSearchServer;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.related.indexing.locationmappers.HourBasedStorageLocationMapper;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateAndHourFormatter;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.related.indexing.util.UTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.*;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 17/06/2013
 * Time: 09:29
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedItemIndexingRepositoryTest {

    private RelatedItemStorageRepository repository;
    private Configuration configuration;
    private UTCCurrentDateFormatter dateFormatter;
    private ElasticSearchClientFactory clientFactory;
    private RelatedItemStorageLocationMapper dayStorageLocationMapper;
    private RelatedItemStorageLocationMapper hourStorageLocationMapper;

    private static String tmpDir;

//    private static EsSetup esSetup;
    private ElasticSearchServer server;

    private String currentIndexDate;

//
    @BeforeClass
    public static void setUpElastic() {

//        String tmpDirectory =  System.getProperty("java.io.tmpdir");
//        String fileSep = System.getProperty("file.separator");
//
//        if(!tmpDirectory.endsWith(fileSep)) tmpDirectory += fileSep;
//        tmpDirectory += UUID.randomUUID().toString() + fileSep;
//
//        tmpDir = tmpDirectory;
//
//        // Instantiates a local node & client
//        configuration = new SystemPropertiesConfiguration();
//
//        esSetup = new EsSetup(ImmutableSettings.settingsBuilder()
//                .put("cluster.name", configuration.getStorageClusterName())
//                .put("index.store.type", "memory")
//                .put("index.store.fs.memory.enabled", "true")
//                .put("gateway.type", "local")
//                .put("node.data", true)
//                .put("node.client",false)
//                .put("node.master", true)
//                .put("discovery.zen.ping.multicast.enabled", "false")
//                .put("discovery.zen.ping.multicast.ping.enabled","false")
//                .put("discovery.zen.ping.unicast.enabled", "true")
//                .put("discovery.zen.ping.unicast.hosts", "127.0.0.1[12345-23456]")
//                .put("path.data", tmpDir+"data")
//                .put("path.work", tmpDir+"work")
//                .put("path.logs", tmpDir+"logs")
//                .put("index.number_of_shards", "1")
//                .put("index.number_of_replicas", "0")
//                .put("cluster.routing.schedule", "50ms")
//                .put("node.local", true)
//                .put("http.enabled", false)
//
////                              .put("cluster.name", configuration.getStorageClusterName())
////                                      .put("index.store.type", "memory")
////                                      .put("index.store.fs.memory.enabled", "true")
////                                      .put("gateway.type", "none")
////                                      .put("index.number_of_shards", "1")
////                                      .put("index.number_of_replicas", "0")
////                                      .put("cluster.routing.schedule", "50ms")
////                                      .put("node.local", true)
////                                      .put("node.data", true)
////                                      .put("discovery.zen.ping.multicast.enabled", "false")
////                                      .put("network.host","127.0.0.1")
////                .put("http.enabled",false)
//                .build());
//
//        esSetup.execute( deleteAll() );
//

        // Clean all data


    }

    @AfterClass
    public static void shutdownElastic() {
//        esSetup.terminate();
    }

    @Before
    public void setUp() {
        configuration = new SystemPropertiesConfiguration();
        server = new ElasticSearchServer(configuration.getStorageClusterName(),false);

        dateFormatter = new JodaUTCCurrentDateFormatter();

        dayStorageLocationMapper = new DayBasedStorageLocationMapper(configuration,dateFormatter);

        hourStorageLocationMapper = new HourBasedStorageLocationMapper(configuration,new JodaUTCCurrentDateAndHourFormatter());

//        esSetup.execute( deleteAll() );
//        esClient = esSetup.client();
        currentIndexDate = dateFormatter.getCurrentDay();

        Settings settings = ImmutableSettings.
                settingsBuilder().
                put("network.host", "127.0.0.1").
                put("cluster.name",configuration.getStorageClusterName()).
                put("node.local", true).
                put("node.data", false).
                put("discovery.zen.ping.multicast.enabled", "false").build();

        clientFactory = new NodeBasedElasticSearchClientFactory(settings,configuration);

        repository = new ElasticSearchRelatedItemIndexingRepository(configuration,clientFactory);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
        // Shutdown the repo
        repository.shutdown();
    }


    private RelatedItem createRelatedItemWithCurrentDay() {
        return createRelatedItemWithGivenDate(currentIndexDate);
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
        assertTrue(server.indexExists(getIndexNamePrefix() + currentIndexDate));

        QueryBuilder qb = termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));


        SearchResponse response = server.getEsClient().prepareSearch(getIndexNamePrefix()+ currentIndexDate).setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
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

    private String getIndexNamePrefix() {
        String indexName = configuration.getStorageIndexNamePrefix();
        if(indexName.endsWith("-")) return indexName;
        else return indexName+"-";
    }

    @Test
    public void testSingleStoreOfProductWithCustomDate() {
        RelatedItem productToStore = createRelatedItemWithGivenDate("2012-05-16");
        indexAndFlush(Arrays.asList(productToStore));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2012-05-16"));

        QueryBuilder qb = termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }


    @Test
    public void testSingleStoreOfProductWithCustomDateAndTime() {
        RelatedItem productToStore = createRelatedItemWithGivenDate("2012-05-15T12:00:00+01:00");
        indexAndFlush(Arrays.asList(productToStore));

        assertTrue(server.indexExists(getIndexNamePrefix() + "2012-05-15"));

        QueryBuilder qb = termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productToStore.getId()));

        SearchResponse response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    @Test
    public void testMultiStoreOfProductWithCustomDateAndToday() {
        RelatedItem[] productsToStore =  new RelatedItem[] {createRelatedItemWithGivenDate("2012-05-15T12:00:00+01:00"),
                                                                 createRelatedItemWithGivenDate("2011-05-15T11:00:00+01:00"),
                                                                 createRelatedItemWithCurrentDay()};

        indexAndFlush(Arrays.asList(productsToStore));

        assertTrue(server.indexExists(getIndexNamePrefix() + "2012-05-15"));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2011-05-15"));
        assertTrue(server.indexExists(getIndexNamePrefix() + currentIndexDate));

        QueryBuilder qb = boolQuery().should(termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[0].getId())))
                .should(termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[1].getId())))
                .should(termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[2].getId())));

        SearchResponse response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(3,response.getHits().getTotalHits());
    }

    @Test
    public void testMultiStoreOfProductWithCustomDateAndTodayWithHourMapper() {
        RelatedItem[] productsToStore =  new RelatedItem[] {createRelatedItemWithGivenDate("2012-05-15T12:00:00+01:00"),
                createRelatedItemWithGivenDate("2011-05-15T11:00:00+01:00"),
                createRelatedItemWithCurrentDay()};

        indexAndFlush(Arrays.asList(productsToStore));

        assertTrue(server.indexExists(getIndexNamePrefix() + "2012-05-15"));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2011-05-15"));
        assertTrue(server.indexExists(getIndexNamePrefix() + currentIndexDate));

        QueryBuilder qb = boolQuery().should(termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[0].getId())))
                .should(termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[1].getId())))
                .should(termQuery(configuration.getKeyForIndexRequestIdAttr(), new String(productsToStore[2].getId())));

        SearchResponse response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(3,response.getHits().getTotalHits());
    }



    @Test
    public void testMultiStoreOfProductWithCustomDateAndProperties() {
        RelatedItem[] productsToStore =  new RelatedItem[] {
                createRelatedItemWithGivenDateAndProperties("2012-05-01T12:00:00+01:00",
                        new HashMap() {{
                            put("location", "london");
                            put("country", "gb");
                        }}),

                createRelatedItemWithGivenDateAndProperties("2011-05-02T11:00:00+01:00",
                        new HashMap() {{
                            put("location", "liverpool");
                        }}),

                createRelatedItemWithGivenDateAndProperties("2011-05-03T10:00:00+01:00",
                        new HashMap() {{
                            put("location", "manchester");
                        }})

        };


        indexAndFlush(hourStorageLocationMapper,Arrays.asList(productsToStore));

        assertTrue(server.indexExists(getIndexNamePrefix() + "2012-05-01_11"));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2011-05-02_10"));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2011-05-03_09"));

        QueryBuilder qb = termQuery("location", "liverpool");



        SearchResponse response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());
    }

    private void indexAndFlush(RelatedItemStorageLocationMapper storageLocationMapper, List<RelatedItem> products) {
        repository.store(storageLocationMapper,products);
        server.getEsClient().admin().indices().refresh(new RefreshRequest(configuration.getStorageIndexNamePrefix() + "*")).actionGet();
    }

    private void indexAndFlush(List<RelatedItem> products) {
       indexAndFlush(dayStorageLocationMapper,products);
    }

    @Test
    public void testShutdown() throws Exception {

    }


}
