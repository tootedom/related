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

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.elastic.NodeBasedElasticSearchClientFactory;
import org.greencheek.related.elastic.http.HttpElasticSearchClientFactory;
import org.greencheek.related.elastic.http.ahc.AHCHttpElasticSearchClientFactory;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.idsQuery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 17/06/2013
 * Time: 09:29
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchRelatedItemDocumentIndexingRepositoryTest {

    private RelatedItemStorageRepository repository;
    private Configuration configuration;
    private UTCCurrentDateFormatter dateFormatter;
    private ElasticSearchClientFactory clientFactory;
    private RelatedItemStorageLocationMapper dayStorageLocationMapper;
    private RelatedItemStorageLocationMapper hourStorageLocationMapper;

    private static String tmpDir;

    private ElasticSearchServer server;

    private String currentIndexDate;


    @Before
    public void setUp() {
        System.setProperty(ConfigurationConstants.PROPNAME_DOCUMENT_INDEXING_ENABLED,"true");
        configuration = new SystemPropertiesConfiguration();

        Settings defaultSettings = ImmutableSettings.
                settingsBuilder().
                put("script.native.relateddocupdater.type","org.greencheek.related.plugins.relateddocsmerger.RelatedDocsMergerFactory").
                build();

        server = new ElasticSearchServer(configuration.getStorageClusterName(),false,true,defaultSettings);

        dateFormatter = new JodaUTCCurrentDateFormatter();

        dayStorageLocationMapper = new DayBasedStorageLocationMapper(configuration,dateFormatter);

        hourStorageLocationMapper = new HourBasedStorageLocationMapper(configuration,new JodaUTCCurrentDateAndHourFormatter());


        currentIndexDate = dateFormatter.getCurrentDay();


    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty(ConfigurationConstants.PROPNAME_DOCUMENT_INDEXING_ENABLED);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS);
        server.shutdown();
        // Shutdown the repo
        if(repository!=null) {
            repository.shutdown();
        }
    }



    private RelatedItem createRelatedItemWithGivenDateAndProperties(String date, Map<String, String> customProps) {
        if(customProps==null) {
            return new RelatedItem(UUID.randomUUID().toString().toCharArray(),date,new char[][]{UUID.randomUUID().toString().toCharArray()},new RelatedItemAdditionalProperties(configuration,0));
        } else {
            return new RelatedItem(UUID.randomUUID().toString().toCharArray(),date,new char[][]{UUID.randomUUID().toString().toCharArray()}, convertFrom(configuration, customProps));
        }
    }




    public static RelatedItemAdditionalProperties convertFrom(Configuration config, Map<String,String> propertes) {
        RelatedItemAdditionalProperties propArrays = new RelatedItemAdditionalProperties(config,propertes.size()+3);

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
    public void testMultiStoreOfProductWithCustomDateAndPropertiesViaHttpRepository() {
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_HTTP_HOSTS,"http://localhost:"+server.getHttpPort());
        configuration = new SystemPropertiesConfiguration();
        HttpElasticSearchClientFactory factory = new AHCHttpElasticSearchClientFactory(configuration);
        repository = new ElasticSearchRelatedItemHttpIndexingRepository(configuration,factory);

        testMultiStoreOfProductWithCustomDateAndProperties(repository);
    }

    @Test
    public void testMultiStoreOfProductWithCustomDateAndProperties() {
        Settings settings = ImmutableSettings.
                settingsBuilder().
                put("network.host", "127.0.0.1").
                put("cluster.name",configuration.getStorageClusterName()).
                put("node.local", true).
                put("node.data", false).
                put("discovery.zen.ping.multicast.enabled", "false").build();

        clientFactory = new NodeBasedElasticSearchClientFactory(settings,configuration);

        repository = new ElasticSearchRelatedItemIndexingRepository(configuration,clientFactory);

        testMultiStoreOfProductWithCustomDateAndProperties(repository);
    }

    public void testMultiStoreOfProductWithCustomDateAndProperties(
            RelatedItemStorageRepository repository) {
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


        indexAndFlush(hourStorageLocationMapper,Arrays.asList(productsToStore),
                repository);

        assertTrue(server.indexExists(getIndexNamePrefix() + "2012-05-01_11"));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2011-05-02_10"));
        assertTrue(server.indexExists(getIndexNamePrefix() + "2011-05-03_09"));
        assertTrue(server.indexExists(configuration.getRelatedItemsDocumentIndexName()));
        QueryBuilder qb = termQuery("location", "liverpool");

        server.flush(configuration.getRelatedItemsDocumentIndexName());
        SearchResponse response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(1,response.getHits().getTotalHits());

        qb = idsQuery(configuration.getRelatedItemsDocumentTypeName()).addIds(new String(productsToStore[0].getId()),new String(productsToStore[1].getId()));
        response = server.getEsClient().prepareSearch(configuration.getRelatedItemsDocumentIndexName()).setQuery(qb).execute().actionGet();
        assertEquals(2,response.getHits().getTotalHits());

        GetResponse res = server.getEsClient().prepareGet(configuration.getRelatedItemsDocumentIndexName(),configuration.getRelatedItemsDocumentTypeName(),new String(productsToStore[0].getId())).execute().actionGet();
        assertEquals(1,res.getVersion());

        indexAndFlush(hourStorageLocationMapper,Arrays.asList(productsToStore),repository);

        server.flush(configuration.getRelatedItemsDocumentIndexName());
        qb = termQuery("location", "liverpool");
        response = server.getEsClient().prepareSearch(configuration.getStorageIndexNamePrefix() + "*").setTypes(configuration.getStorageContentTypeName()).setQuery(qb).execute().actionGet();

        assertEquals(2,response.getHits().getTotalHits());

        res = server.getEsClient().prepareGet(configuration.getRelatedItemsDocumentIndexName(),configuration.getRelatedItemsDocumentTypeName(),new String(productsToStore[0].getId())).execute().actionGet();
        assertEquals(1,res.getVersion());

        productsToStore[0].getAdditionalProperties().addProperty("type","book");
        indexAndFlush(hourStorageLocationMapper,Arrays.asList(productsToStore),repository);
        server.flush(configuration.getRelatedItemsDocumentIndexName());

//        qb = idsQuery(configuration.getRelatedItemsDocumentTypeName()).addIds(new String(productsToStore[0].getId()),new String(productsToStore[1].getId()));
//        response = server.getEsClient().prepareSearch(configuration.getRelatedItemsDocumentIndexName()).setQuery(qb).execute().actionGet();
//        assertEquals(2,response.getHits().getTotalHits());
//
//        assertEquals(response.getHits().getAt(0).getVersion(),2);

        res = server.getEsClient().prepareGet(configuration.getRelatedItemsDocumentIndexName(),configuration.getRelatedItemsDocumentTypeName(),new String(productsToStore[0].getId())).execute().actionGet();
        assertEquals(2,res.getVersion());


    }

    private void indexAndFlush(RelatedItemStorageLocationMapper storageLocationMapper,
                               List<RelatedItem> products,
                               RelatedItemStorageRepository repository) {
        repository.store(storageLocationMapper, products);
        server.flush(configuration.getStorageIndexNamePrefix() + "*");
    }
}
