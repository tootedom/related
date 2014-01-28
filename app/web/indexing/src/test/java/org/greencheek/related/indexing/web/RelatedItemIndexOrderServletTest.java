package org.greencheek.related.indexing.web;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.ning.http.client.generators.InputStreamBodyGenerator;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.indexing.web.bootstrap.BootstrapApplicationCtx;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.YamlSystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Really this is truely an integration test that checks:
 *
 * - Request is handled by the servlet, passed to the ring buffer, converted and
 * sent to the storage repository for saving.
 *
 */
public class RelatedItemIndexOrderServletTest {

    private final String mWorkingDir = System.getProperty("java.io.tmpdir");

    private Tomcat tomcat;
    private AsyncHttpClient asyncHttpClient;
    private String indexingurl;

    private final String POST_JSON = "{\n" +
            "   \"channel\":\"de\",\n" +
            "   \"site\":\"amazon\",\n" +
            "   \"items\":[\n" +
            "      {\n" +
            "         \"id\":\"1\",\n" +
            "         \"type\":\"map\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"2\",\n" +
            "         \"type\":\"compass\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"id\":\"3\",\n" +
            "         \"type\":\"torch\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    private final String POST_JSON_NO_PRODUCTS = "{\n" +
            "   \"channel\":\"de\",\n" +
            "   \"site\":\"amazon\",\n" +
            "   \"items\":[]\n" +
            "}";

    @Before
    public void setUp() {

    }

    public void startTomcat(BootstrapApplicationCtx bootstrapApplicationCtx) {
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

        Wrapper wrapper = tomcat.addServlet("/indexing","Indexing","org.greencheek.related.indexing.web.RelatedItemIndexOrderServlet");
        wrapper.setAsyncSupported(true);
        wrapper.addMapping("/relateditems");

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

        asyncHttpClient = new AsyncHttpClient();
        indexingurl = "http://localhost:" + getTomcatPort() +"/indexing/relateditems";

    }


    @After
    public final void teardown() throws Throwable {
        System.clearProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE);
        System.clearProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        System.clearProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        System.clearProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES);
        System.clearProperty(ConfigurationConstants.PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE);
        try {
            shutdownTomcat();
        } catch (Exception e) {

        }

        asyncHttpClient.close();

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

    /**
     * Test that indexing requests are sent to a single storage repository
     * i.e. traverses the ring buffer to the storage repo
     */
    @Test
    public void testIndexingSingleItemWithSingleRequestProcessor() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS, "1");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }



        Response response1 = sendPost();

        try {
            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);

        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }



        int i = 0;
        int reposCalled = 0;
        for(TestRelatedItemStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
            if(repo.getProductsRequestedToBeStored()>1) reposCalled++;
        }

        assertEquals(3,i);
        assertEquals(1,reposCalled);
    }


    /**
     * Test that indexing requests are round robin to both the storage repositories
     * i.e. traverses the ring buffers to the storage repo
     */
    @Test
    public void testIndexingSingleItemWithMultipleRequestProcessor() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"2");

        final CountDownLatch latch = new CountDownLatch(15);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response = sendPost();
        response = sendPost();
        response = sendPost();
        response = sendPost();
        response = sendPost();

        try {
            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);
        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }

        int i = 0;
        int reposCalled = 0;
        for(TestRelatedItemStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
            if(repo.getProductsRequestedToBeStored()>0) reposCalled++;
        }

        assertEquals(15,i);
        assertEquals(2,reposCalled);
    }

    /**
     * Test that we can exceed the minimum post size
     *
     */
    @Test
    public void testIndexingLargePostDataGreaterThanMinimum() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "16");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "1024");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }



        Response response1 = sendPost();

        try {
            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);

        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }



        int i = 0;
        int reposCalled = 0;

        for(TestRelatedItemStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
            if(repo.getProductsRequestedToBeStored()>0) reposCalled++;

        }

        assertEquals(3,i);
        assertEquals(1,reposCalled);
    }

    /**
     * Test that we can exceed the minimum post size
     *
     */
    @Test
    public void testChunkedEncoding() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "16");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "1024");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response1 = sendPostChunked();

        try {
            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);

        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }



        int i = 0;
        int reposCalled = 0;

        for(TestRelatedItemStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
            if(repo.getProductsRequestedToBeStored()>0) reposCalled++;

        }

        assertEquals(3,i);
        assertEquals(1,reposCalled);
    }

    /**
     * Test that if we the maximum post size, a 413 is sent
     * back to the client
     */
    @Test
    public void testChunkedEncodingWithLargerThanMaxPostSize() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "16");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "32");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response1 = sendPostChunked(413);

    }

    /**
     * Test that if we the maximum post size, a 413 is sent
     * back to the client
     */
    @Test
    public void testEmptyPostDataResultsInA400() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "16");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "32");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response1 = sendPostNoData(400);

        response1 = sendPostNoDataChunked(400);
    }


    /**
     * Test that if we the maximum post size, a 413 is sent
     * back to the client
     */
    @Test
    public void testInvalidContentLengthHeader() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "16");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "32");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        sendPostWithLargeContentLength(413);

    }

    /**
     * Test that if we the maximum post size, a 413 is sent
     * back to the client
     */
    @Test
    public void testInvalidContentWithNoProducts() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_MIN_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "16");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_RELATED_ITEM_POST_DATA_SIZE_IN_BYTES, "1024");

        final CountDownLatch latch = new CountDownLatch(3);
        TestBootstrapApplicationCtx bootstrap = getTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        sendPostWithGivenBody(400,POST_JSON_NO_PRODUCTS.getBytes());

    }

    @Test
    public void testBackPressureResultsIn503() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE,"3");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"1");
        System.setProperty(ConfigurationConstants.PROPNAME_SIZE_OF_INCOMING_REQUEST_QUEUE, "2");


        final CountDownLatch latch = new CountDownLatch(6);
        TestBootstrapApplicationCtx bootstrap = getSlowTestBootStrap(latch);
        try {
            startTomcat(bootstrap);
        } catch(Exception e) {
            try {
                shutdownTomcat();
            } catch (Exception shutdown) {

            }
            fail("Unable to start tomcat");
        }

        Response response = sendPost(202);
        response = sendPost(202);
        response = sendPost(503);
        response = sendPost(503);
        response = sendPost(503);


        try {
            boolean countedDown = latch.await(6000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);
        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }


        int i = 0;
        int reposCalled = 0;

        for(TestRelatedItemStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
            if(repo.getProductsRequestedToBeStored()>0) reposCalled++;

        }

        assertEquals(6,i);
        assertEquals(1,reposCalled);
    }


    /**
     * sends the indexing request and asserts that a http 202 was received.
     * @return
     */
    private Response sendPost() {
        return sendPost(202);
    }


    /**
     * sends the indexing request and asserts that a http 202 was received.
     * @return
     */
    private Response sendPost(int statusCodeExpected) {
        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setBody(POST_JSON).execute().get();
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

    /**
     * sends the indexing request and asserts that a http 202 was received.
     * This sends the post data chunked
     * @return
     */
    private Response sendPostChunked() {
        return sendPostChunked(202);
    }


    /**
     * sends the indexing request and asserts that the given http status code
     * was received.  The post request is sent using chunked encoding.
     * @return
     */
    private Response sendPostChunked(int statusExpected) {

        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setBody(new
                    InputStreamBodyGenerator(new ByteArrayInputStream(POST_JSON.getBytes()))).setContentLength(-1).execute().get();
            assertEquals(statusExpected, response.getStatusCode());
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

    /**
     * Sends the indexing request and asserts that the given http status code
     * was received.  However, no data is
     * actually sent in the request.
     *
     * @return
     */
    private Response sendPostNoData(int statusExpected) {

        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setBody(new byte[0]).setContentLength(0).execute().get();
            assertEquals(statusExpected, response.getStatusCode());
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

    /**
     * Sends the indexing request and asserts that the given http status code
     * was received.  However, no data is
     * actually sent in the request.
     *
     * @return
     */
    private Response sendPostNoDataChunked(int statusExpected) {

        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setBody(new InputStreamBodyGenerator(new ByteArrayInputStream(new byte[0]))).execute().get();
            assertEquals(statusExpected, response.getStatusCode());
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

    /**
     * Sends the indexing request and asserts that the given http status code
     * was received.  However, no data is
     * actually sent in the request.
     *
     * @return
     */
    private Response sendPostWithLargeContentLength(int statusExpected) {

        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setHeader("Content-Length","" + Long.MAX_VALUE).execute().get();
            assertEquals(statusExpected, response.getStatusCode());
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

    /**
     * Sends the indexing request and asserts that the given http status code
     * was received.  However, no data is
     * actually sent in the request.
     *
     * @return
     */
    private Response sendPostWithGivenBody(int statusExpected, byte[] body) {

        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setBody(body).execute().get();
            assertEquals(statusExpected, response.getStatusCode());
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


    protected int getTomcatPort() {
        return tomcat.getConnector().getLocalPort();
    }



    public TestBootstrapApplicationCtx getTestBootStrap(CountDownLatch latch) {
        return new TestBootstrapApplicationCtx(new TestRelatedItemStorageRepositoryFactory(latch));
    }

    public TestBootstrapApplicationCtx getSlowTestBootStrap(CountDownLatch latch) {
        return new TestBootstrapApplicationCtx(new SlowTestRelatedItemStorageRepositoryFactory(latch));
    }

    public class TestBootstrapApplicationCtx extends BootstrapApplicationCtx {

        private volatile TestRelatedItemStorageRepositoryFactory repository;

        public TestBootstrapApplicationCtx(TestRelatedItemStorageRepositoryFactory repositoryFactory) {
            this.repository = repositoryFactory;
        }

        public RelatedItemStorageRepositoryFactory getStorageRepositoryFactory(Configuration applicationConfiguration) {
            return repository;
        }

        public Configuration createConfiguration() {
            return new YamlSystemPropertiesConfiguration();
        }

        public TestRelatedItemStorageRepositoryFactory getRepository() {
            return repository;
        }
    }

    public class TestRelatedItemStorageRepositoryFactory implements RelatedItemStorageRepositoryFactory {
        List<TestRelatedItemStorageRepository> repos = new CopyOnWriteArrayList<>();

        final CountDownLatch latch;

        public TestRelatedItemStorageRepositoryFactory(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public RelatedItemStorageRepository getRepository(Configuration configuration) {
            TestRelatedItemStorageRepository repo = createRepo();
            repos.add(repo);
            return repo;
        }

        public TestRelatedItemStorageRepository createRepo() {
            return new TestRelatedItemStorageRepository(latch);
        }

        public List<TestRelatedItemStorageRepository> getRepos() {
            return repos;
        }
    }

    public class TestRelatedItemStorageRepository implements RelatedItemStorageRepository {
        AtomicInteger itemsRequestedToBeStored = new AtomicInteger(0);
        AtomicBoolean shutdown = new AtomicBoolean(false);

        final CountDownLatch latch;

        public TestRelatedItemStorageRepository(CountDownLatch latch)
        {
            this.latch = latch;
        }

        @Override
        public void store(RelatedItemStorageLocationMapper indexToMapper, List<RelatedItem> relatedItems) {
            itemsRequestedToBeStored.addAndGet(relatedItems.size());
            for(int i=0;i< relatedItems.size();i++) latch.countDown();
        }

        @Override
        public void shutdown() {
            shutdown.set(true);
        }

        public int getProductsRequestedToBeStored() {
            return itemsRequestedToBeStored.get();
        }

        public boolean isShutdown() {
            return shutdown.get();
        }
    }

    public class SlowTestRelatedItemStorageRepositoryFactory extends TestRelatedItemStorageRepositoryFactory {

        public SlowTestRelatedItemStorageRepositoryFactory(CountDownLatch latch) {
            super(latch);
        }

        public TestRelatedItemStorageRepository createRepo() {
            return new SlowTestRelatedItemStorageRepository(latch);
        }
    }

    public class SlowTestRelatedItemStorageRepository extends TestRelatedItemStorageRepository {
        AtomicInteger itemsRequestedToBeStored = new AtomicInteger(0);
        AtomicBoolean shutdown = new AtomicBoolean(false);
        private final long sleepTime;

        public SlowTestRelatedItemStorageRepository(CountDownLatch latch)
        {
            super(latch);
            long sleepTime;
            try {
                sleepTime = Long.parseLong(System.getProperty("test.slow.repo.sleepTime","3000"));
            } catch(NumberFormatException e) {
                sleepTime = 2000;
            }
            this.sleepTime = sleepTime;
        }

        @Override
        public void store(RelatedItemStorageLocationMapper indexToMapper, List<RelatedItem> relatedItems) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println("sleep interuptted");
                System.err.flush();
            }
            super.store(indexToMapper, relatedItems);
        }


    }

}
