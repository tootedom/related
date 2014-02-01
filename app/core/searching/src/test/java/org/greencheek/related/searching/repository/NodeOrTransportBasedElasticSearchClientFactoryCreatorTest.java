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

package org.greencheek.related.searching.repository;

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.greencheek.related.elastic.ElasticSearchClientFactory;
import org.greencheek.related.searching.util.elasticsearch.ElasticSearchServer;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests that both the node and the transport client can be created based on
 * configuration.
 */
public class NodeOrTransportBasedElasticSearchClientFactoryCreatorTest {

    private ElasticSearchServer esServer;
    private Configuration configuration;
    private int port;
    private ElasticSearchClientFactoryCreator clientFactoryCreator = new NodeOrTransportBasedElasticSearchClientFactoryCreator();
    private String clusterName;

    @Before
    public void setUp() throws IOException {
        clusterName = UUID.randomUUID().toString();
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME, clusterName);
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_ES_CLIENT_TYPE);
        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME);

        esServer.shutdown();
    }

    @Test
    public void testTransportBasedClientCanConnectToES() {
        esServer = new ElasticSearchServer(clusterName,true);

        assertTrue("Unable to start in memory elastic search", esServer.isSetup());

        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS, "localhost:" + esServer.getPort());
        System.setProperty(ConfigurationConstants.PROPNAME_ES_CLIENT_TYPE, "transport");
        Configuration config = new SystemPropertiesConfiguration();

        try {
            ElasticSearchClientFactory factory = clientFactoryCreator.getElasticSearchClientConnectionFactory(config);
            Client c = factory.getClient();
            assertEquals(config.getStorageClusterName(), c.admin().cluster().nodesInfo(new NodesInfoRequest()).actionGet().getClusterName().value());
            assertNotNull(c.index(new IndexRequest("test", "test").source("name", "a")).actionGet().getId());
            c.admin().indices().refresh(new RefreshRequest("test").force(true)).actionGet();

            assertEquals(1,c.admin().indices().stats(new IndicesStatsRequest().indices("test")).actionGet().getTotal().docs.getCount());
        } catch(Exception e) {
            fail("Unable to connect to elasticsearch: " + e.getMessage());
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testNodeBasedClientCanConnectToES() {
        System.setProperty(ConfigurationConstants.PROPNAME_ES_CLIENT_TYPE, "node");
        Configuration config = new SystemPropertiesConfiguration();

        esServer = new ElasticSearchServer(clusterName,false);
        assertTrue("Unable to start in memory elastic search", esServer.isSetup());

        try {
            ElasticSearchClientFactory factory = clientFactoryCreator.getElasticSearchClientConnectionFactory(config);
            Client c = factory.getClient();
            assertEquals(config.getStorageClusterName(), c.admin().cluster().nodesInfo(new NodesInfoRequest()).actionGet().getClusterName().value());

            assertNotNull(c.index(new IndexRequest("test", "test").source("name", "a")).actionGet().getId());
            c.admin().indices().refresh(new RefreshRequest("test").force(true)).actionGet();

            assertEquals(1, c.admin().indices().stats(new IndicesStatsRequest().indices("test")).actionGet().getTotal().docs.getCount());
        } catch(Exception e) {
            e.printStackTrace();
            fail("Unable to connect to elasticsearch: " + e.getMessage());
        }
    }



}
