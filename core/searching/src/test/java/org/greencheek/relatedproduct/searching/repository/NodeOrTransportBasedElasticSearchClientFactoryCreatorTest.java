package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.util.elasticsearch.ElasticSearchServer;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
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
        System.setProperty("related-product.storage.cluster.name", clusterName);
    }

    @After
    public void tearDown() {
        System.clearProperty("related-product.elastic.search.transport.hosts");
        System.clearProperty("related-product.es.client.type");
        System.clearProperty("related-product.elastic.search.client.default.settings.file.name");
        System.clearProperty("related-product.storage.cluster.name");

        esServer.shutdown();
    }

    @Test
    public void testTransportBasedClientCanConnectToES() {
        esServer = new ElasticSearchServer(clusterName,true);

        assertTrue("Unable to start in memory elastic search", esServer.isSetup());

        System.setProperty("related-product.elastic.search.transport.hosts", "localhost:" + esServer.getPort());
        System.setProperty("related-product.es.client.type", "transport");
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
        System.setProperty("related-product.es.client.type", "node");
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
