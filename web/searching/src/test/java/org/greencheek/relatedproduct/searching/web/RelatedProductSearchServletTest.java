package org.greencheek.relatedproduct.searching.web;

import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.web.bootstrap.SearchBootstrapApplicationCtx;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.util.elasticsearch.ElasticSearchServer;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration test for testing that search requests can be executed
 */
public class RelatedProductSearchServletTest {

    private final String mWorkingDir = System.getProperty("java.io.tmpdir");

    private Tomcat tomcat;
    private AsyncHttpClient asyncHttpClient;
    private String indexingurl;
    private Configuration configuration;

    private ElasticSearchServer server;
    private ElasticSearchClientFactory factory;
    RelatedProductSearchRepository<FrequentlyRelatedSearchResult[]> repository;



    @Before
    public void setUp() {
        String indexName = "relatedprog";
        System.setProperty("related-product.storage.index.name.prefix",indexName);
        // Set the clustername
        System.setProperty("related-product.storage.cluster.name", "relatedprogrammes");
        configuration = new SystemPropertiesConfiguration();

        // Start the Elastic Search Server
        server = new ElasticSearchServer(configuration.getStorageClusterName(),true);

        if(!server.isSetup()) throw new RuntimeException("ElasticSearch Not set");

        server.setIndexTemplate(indexName);

        // Create the client pointing to the above server
        System.setProperty("related-product.elastic.search.transport.hosts","localhost:" + server.getPort());
        configuration = new SystemPropertiesConfiguration();
        factory = new TransportBasedElasticSearchClientFactory(configuration);

        // Create the repo
        repository = new ElasticSearchRelatedProductSearchRepository(factory,new ElasticSearchFrequentlyRelatedProductSearchProcessor(configuration));

        try {
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-14",RELATED_CONTENT_BLADES1_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-14",RELATED_CONTENT_BLADES1_PURCHASEb);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEa);
            server.indexDocument(configuration.getStorageIndexNamePrefix()+"-2013-12-15",RELATED_CONTENT_BLADES2_PURCHASEb);


            assertEquals(2,server.getIndexCount());
            assertEquals(2,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-14"));
            assertEquals(2,server.getDocCount(configuration.getStorageIndexNamePrefix()+"-2013-12-15"));
        } catch(Exception e)  {
            fail("Cannot create test date for search test");
        }
    }

    @After
    public final void teardown() throws Throwable {
        System.clearProperty("related-product.index.batch.size");
        System.clearProperty("related-product.number.of.indexing.request.processors");
        System.clearProperty("related-product.min.related.product.post.data.size.in.bytes");
        System.clearProperty("related-product.max.related.product.post.data.size.in.bytes");
        System.clearProperty("related-product.size.of.incoming.request.queue");
        try {
            shutdownTomcat();
        } catch (Exception e) {

        }

        if(asyncHttpClient!=null) {
            try {
                asyncHttpClient.close();
            } catch(Exception e) {

            }
        }


        System.clearProperty("related-product.storage.index.name.prefix");
        System.clearProperty("related-product.storage.cluster.name");
        System.clearProperty("related-product.elastic.search.transport.hosts");
        System.clearProperty("related-product.frequently.related.search.timeout.in.millis");
        if(server!=null) {
            server.shutdown();
        }

        factory.shutdown();

    }

    public final void shutdownTomcat() throws Exception  {
        if (tomcat.getServer() != null
                && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
            if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
                tomcat.stop();
            }
            tomcat.destroy();
        }
        tomcat.getServer().await();
    }



    protected int getTomcatPort() {
        return tomcat.getConnector().getLocalPort();
    }



    public void startTomcat(SearchBootstrapApplicationCtx bootstrapApplicationCtx) {
        String webappDirLocation = "src/main/webapp/";
        tomcat = new Tomcat();
        tomcat.setPort(0);
        tomcat.setBaseDir(mWorkingDir);
        tomcat.getHost().setAppBase(mWorkingDir);
        tomcat.getHost().setAutoDeploy(true);
        tomcat.getHost().setDeployOnStartup(true);

        Context ctx = tomcat.addContext(tomcat.getHost(),"/indexing","/");

        ((StandardContext)ctx).setProcessTlds(false);  // disable tld processing.. we don't use any
        ctx.addParameter("com.sun.faces.forceLoadConfiguration","false");

        Wrapper wrapper = tomcat.addServlet("/indexing","Indexing","org.greencheek.relatedproduct.indexing.web.RelatedPurchaseIndexOrderServlet");
        wrapper.setAsyncSupported(true);
        wrapper.addMapping("/relatedproducts");

        ctx.getServletContext().setAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME,bootstrapApplicationCtx);

        //declare an alternate location for your "WEB-INF/classes" dir:
        File additionWebInfClasses = new File("target/classes");
        VirtualDirContext resources = new VirtualDirContext();
        resources.setExtraResourcePaths("/WEB-INF/classes=" + additionWebInfClasses);
        ctx.setResources(resources);

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        asyncHttpClient = new AsyncHttpClient();
        indexingurl = "http://localhost:" + getTomcatPort() +"/indexing/relatedproducts";

    }



    /**
     * Test that indexing requests are sent to a single storage repository
     * i.e. traverses the ring buffer to the storage repo
     */
    @Test
    public void testSearchingSingleItemWithSingleRequestProcessor() {
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors", "1");
//
//        final CountDownLatch latch = new CountDownLatch(3);
//        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
//        try {
//            startTomcat(bootstrap);
//        } catch(Exception e) {
//            try {
//                shutdownTomcat();
//            } catch (Exception shutdown) {
//
//            }
//            fail("Unable to start tomcat");
//        }
//
//
//
//        Response response1 = sendPost();
//
//        try {
//            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
//            assertTrue("Storage Repository not called in required time",countedDown);
//
//        } catch (Exception e) {
//            fail("Storage Repository not called in required time");
//        }
//
//
//
//        int i = 0;
//        int reposCalled = 0;
//        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
//            i+= repo.getProductsRequestedToBeStored();
//            if(repo.getProductsRequestedToBeStored()>1) reposCalled++;
//        }
//
//        assertEquals(3,i);
//        assertEquals(1,reposCalled);
    }



    private final static String RELATED_CONTENT_BLADES1_PURCHASEa = "{\n"+
            "\"id\": \"anchorman\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES1_PURCHASEb = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-14T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchor man\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEa = "{\n"+
            "\"id\": \"blades of glory\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"anchorman\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";

    private final static String RELATED_CONTENT_BLADES2_PURCHASEb = "{\n"+
            "\"id\": \"anchorman\",\n"+
            "\"date\": \"2013-12-15T17:44:41.943Z\",\n"+
            "\"related-with\": [ \"blades of glory\",\"dodgeball\" ],\n"+
            "\"type\": \"dvd\",\n"+
            "\"site\": \"amazon\",\n"+
            "\"channel\": \"uk\"\n"+
            "}";


}
