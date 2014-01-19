package org.greencheek.relatedproduct.searching.web;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.elastic.ElasticSearchClientFactory;
import org.greencheek.relatedproduct.elastic.TransportBasedElasticSearchClientFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutorFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.executor.SearchExecutorFactory;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchFrequentlyRelatedProductSearchProcessor;
import org.greencheek.relatedproduct.searching.repository.ElasticSearchRelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.util.elasticsearch.ElasticSearchServer;
import org.greencheek.relatedproduct.searching.web.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.searching.web.bootstrap.SearchBootstrapApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.ConfigurationConstants;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Integration test for testing that search requests can be executed
 */
public class RelatedProductSearchServletTest {

    private final String mWorkingDir = System.getProperty("java.io.tmpdir");

    private Tomcat tomcat;
    private AsyncHttpClient asyncHttpClient;
    private String searchurl;
    private Configuration configuration;

    private ElasticSearchServer server;
    private ElasticSearchClientFactory factory;
    RelatedProductSearchRepository<FrequentlyRelatedSearchResult[]> repository;



    @Before
    public void setUp() {
        String indexName = "relatedprog";
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_INDEX_NAME_PREFIX,indexName);
        // Set the clustername
        System.setProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME, "relatedprogrammes");
        configuration = new SystemPropertiesConfiguration();

        // Start the Elastic Search Server
        server = new ElasticSearchServer(configuration.getStorageClusterName(),true);

        if(!server.isSetup()) throw new RuntimeException("ElasticSearch Not set");

        server.setIndexTemplate(indexName);

        // Create the client pointing to the above server
        System.setProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS,"localhost:" + server.getPort());
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

        System.out.println("===========");
        System.out.println("Setup");
        System.out.println("===========");
    }

    @After
    public final void teardown() throws Throwable {
        System.clearProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE);
        System.clearProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS);
        System.clearProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE);
        System.clearProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RESPONSE_PROCESSING_QUEUE);


        factory.shutdown();


        if(server!=null) {
            server.shutdown();
        }


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


        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_CLUSTER_NAME);
        System.clearProperty(ConfigurationConstants.PROPNAME_STORAGE_INDEX_NAME_PREFIX);
        System.clearProperty(ConfigurationConstants.PROPNAME_ELASTIC_SEARCH_TRANSPORT_HOSTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_FREQUENTLY_RELATED_SEARCH_TIMEOUT_IN_MILLIS);



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

        Context ctx = tomcat.addContext(tomcat.getHost(),"/search","/");

        ((StandardContext)ctx).setProcessTlds(false);  // disable tld processing.. we don't use any
        ctx.addParameter("com.sun.faces.forceLoadConfiguration","false");

        Wrapper wrapper = tomcat.addServlet("/search","Searching","org.greencheek.relatedproduct.searching.web.RelatedProductSearchServlet");
        wrapper.setAsyncSupported(true);
        wrapper.addMapping("/frequentlyrelatedto/*");

        ctx.getServletContext().setAttribute(ConfigurationConstants.APPLICATION_CONTEXT_ATTRIBUTE_NAME,bootstrapApplicationCtx);

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

        AsyncHttpClientConfig.Builder b = new AsyncHttpClientConfig.Builder();
        b.setRequestTimeoutInMs(10000);
        b.setConnectionTimeoutInMs(5000);
        b.setMaxRequestRetry(0);
        asyncHttpClient = new AsyncHttpClient(b.build());
        searchurl = "http://localhost:" + getTomcatPort() +"/search/frequentlyrelatedto";

    }


    /**
     * Test that with an empty id, a 400 is thrown by the servlet
     */
    @Test
    public void test400ReturnedForNoId() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, "1");

        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        sendGet(400, "");
    }

    /**
     * Test that indexing requests are sent to a single storage repository
     * i.e. traverses the ring buffer to the storage repo
     */
    @Test
    public void testSearchingSingleItemWithSingleRequestProcessorReturns200() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, "1");
//
//        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response1 = sendGet(200, "anchorman"); 
    }

    @Test
    public void testBufferSizeIsAdjustedToAPowerOf2() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,"10");
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        assertEquals(16, bootstrap.getConfiguration().getSizeOfRelatedContentSearchRequestQueue());
    }

    @Test
    public void testBufferSizeIsKeptToSetPowerOf2() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,"16");
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        assertEquals(16, bootstrap.getConfiguration().getSizeOfRelatedContentSearchRequestQueue());
    }

    /**
     * Test that indexing requests are sent to a single storage repository
     * i.e. traverses the ring buffer to the storage repo
     */
    @Test
    public void testSearchingMultipleRequestsReturns200WhenQueueNot() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,"10");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, "2");

//
//        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap();
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        List<ListenableFuture<Response>> resps = sendGet("anchorman",10);
        int oks = 0;
        int gatewayBusy = 0;
        int unknown = 0;
        for(ListenableFuture<Response> r : resps) {
            Response res = null;
            try {
                res = r.get();
                switch(res.getStatusCode()) {
                    case 503:
                        gatewayBusy++;
                        break;
                    case 200:
                        oks++;
                        break;
                    default:
                        unknown++;
                        break;
                }
            } catch(Exception e) {
                unknown++;
            }

        }

        assertEquals("1 Request should have been ok",10,oks);
        assertEquals("2 Requests should have been rejected",0,gatewayBusy);
        assertEquals("No requests should have failed with unexpected statuscode",0,unknown);
    }

    @Test
    public void testSearchingMultipleRequestsReturns503WhenQueueFull() {
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_QUEUE,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS, "1");
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_RELATED_CONTENT_SEARCH_REQUEST_AND_RESPONSE_QUEUE,"1");
//
//        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrapSlowGet(3000);
        assertEquals(1, bootstrap.getConfiguration().getSizeOfRelatedContentSearchRequestQueue());

        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        List<ListenableFuture<Response>> resps = sendGet("anchorman",3);
        int oks = 0;
        int gatewayBusy = 0;
        int unknown = 0;
        for(ListenableFuture<Response> r : resps) {
            Response res = null;
            try {
                res = r.get();
                switch(res.getStatusCode()) {
                    case 503:
                        gatewayBusy++;
                        break;
                    case 200:
                        oks++;
                        break;
                    default:
                        unknown++;
                        break;
                }
            } catch(Exception e) {
                unknown++;
            }

        }

        assertEquals("1 Request should have been ok",1,oks);
        assertEquals("2 Requests should have been rejected",2,gatewayBusy);
        assertEquals("No requests should have failed with unexpected statuscode",0,unknown);
    }
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


    /**
     * sends the indexing request and asserts that a http 202 was received.
     * @return
     */
    private Response sendGet(int statusCodeExpected,String id) {
        Response response=null;
        try {
            response = asyncHttpClient.prepareGet(searchurl+"/"+id).execute().get();
            assertEquals(statusCodeExpected, response.getStatusCode());
            return response;
        } catch (IOException e ) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }
        return null;
    }

    private List<ListenableFuture<Response>> sendGet(String id, int numberOfRequests) {

        List<ListenableFuture<Response>> responses = new ArrayList<ListenableFuture<Response>>(numberOfRequests);
        try {
            for(int i =0;i<numberOfRequests;i++) responses.add(asyncHttpClient.prepareGet(searchurl+"/"+id).execute());

            return responses;
        } catch (IOException e ) {
            fail(e.getMessage());
        }
        return null;
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



    public TestBootstrapApplicationCtx getTestBootStrap() {
        return new TestBootstrapApplicationCtx(false,false,0,0);
    }

    public TestBootstrapApplicationCtx getTestBootStrapSlowGet(int timeoutInMs) {
        return new TestBootstrapApplicationCtx(true,false,timeoutInMs,0);
    }

    public TestBootstrapApplicationCtx getTestBootStrapSlowPut(int timeoutInMs) {
        return new TestBootstrapApplicationCtx(false,true,0,timeoutInMs);
    }

    public class TestBootstrapApplicationCtx extends SearchBootstrapApplicationCtx {

        private final boolean slowGet;
        private final boolean slowPut;
        private final long getTimeoutMs;
        private final long putTimeoutMs;

        public TestBootstrapApplicationCtx(boolean slowGet,boolean slowPut,int getTimeoutInMs, int putTimeoutInMs) {
            super();
            this.slowGet = slowGet;
            this.slowPut = slowPut;
            this.getTimeoutMs = getTimeoutInMs;
            this.putTimeoutMs = putTimeoutInMs;
        }

        @Override
        public SearchResponseContextLookup getResponseContextLookup() {
            return new SlowMultiMapSearchResponseContextLookup(slowGet,slowPut,getConfiguration(),
                    (getTimeoutMs==0) ? putTimeoutMs : getTimeoutMs );
        }

        public RelatedProductSearchExecutorFactory createSearchExecutorFactory() {
            return new SlowRelatedProductSearchExecutorFactory(this,(getTimeoutMs==0) ? putTimeoutMs : getTimeoutMs);
        }



    }

    public class SlowRelatedProductSearchExecutorFactory extends SearchExecutorFactory {

        private final long timeout;
        public SlowRelatedProductSearchExecutorFactory(ApplicationCtx ctx, long timeout) {
            super(ctx);
            this.timeout = timeout;
        }

        public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchResultsToResponseGateway gateway) {
            RelatedProductSearchExecutor executor = super.createSearchExecutor(gateway);
            return new SlowRelatedProductSearchExecutor(executor,timeout);
        }

    }

    public class SlowRelatedProductSearchExecutor implements RelatedProductSearchExecutor {

        private final long timeoutInMs;
        private final RelatedProductSearchExecutor executor;
        public SlowRelatedProductSearchExecutor(RelatedProductSearchExecutor executor,long timeoutInMs) {
            this.executor = executor;
            this.timeoutInMs = timeoutInMs;
        }

        @Override
        public void executeSearch(RelatedProductSearch searchRequest) {
            try {
                Thread.sleep(timeoutInMs);
            } catch (Exception e) {

            }
            executor.executeSearch(searchRequest);
        }

        @Override
        public void shutdown() {
            executor.shutdown();
        }
    }



    public class SlowMultiMapSearchResponseContextLookup extends MultiMapSearchResponseContextLookup {

        private final long sleepTime;
        private final boolean slowGet;
        private final boolean slowPut;

        public SlowMultiMapSearchResponseContextLookup(boolean slowGet, boolean slowPut, Configuration config, long timeout) {
            super(config);
            this.slowGet = slowGet;
            this.slowPut = slowPut;
            long sleepTime;
            try {
                sleepTime = Long.parseLong(System.getProperty("test.slow.repo.sleepTime",""+timeout));
            } catch(NumberFormatException e) {
                sleepTime = timeout;
            }
            this.sleepTime = sleepTime;

        }

        public SearchResponseContext[] removeContexts(SearchRequestLookupKey key) {
            try {
                if(slowPut) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

            }
            return super.removeContexts(key);
        }

        public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] context) {
            try {
                if(slowGet) Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

            }
            return super.addContext(key,context);
        }
    }


}
