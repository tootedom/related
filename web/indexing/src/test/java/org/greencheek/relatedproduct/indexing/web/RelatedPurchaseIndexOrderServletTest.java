package org.greencheek.relatedproduct.indexing.web;

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
import org.greencheek.relatedproduct.api.indexing.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.bootstrap.BootstrapApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Really this is truely an integration test that checks:
 *
 * - Request is handled by the servlet, passed to the ring buffer, converted and
 * sent to the storage repository for saving.
 *
 */
public class RelatedPurchaseIndexOrderServletTest {

    private final String mWorkingDir = System.getProperty("java.io.tmpdir");

    private Tomcat tomcat;
    private AsyncHttpClient asyncHttpClient;
    private String indexingurl;

    private final String POST_JSON = "{\n" +
            "   \"channel\":\"de\",\n" +
            "   \"site\":\"amazon\",\n" +
            "   \"products\":[\n" +
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
            "   \"products\":[]\n" +
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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors", "1");

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
        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","2");

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
        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.min.related.product.post.data.size.in.bytes", "16");
        System.setProperty("related-product.max.related.product.post.data.size.in.bytes", "1024");

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

        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.min.related.product.post.data.size.in.bytes","16");
        System.setProperty("related-product.max.related.product.post.data.size.in.bytes","1024");

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

        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.min.related.product.post.data.size.in.bytes","16");
        System.setProperty("related-product.max.related.product.post.data.size.in.bytes","32");

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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.min.related.product.post.data.size.in.bytes","16");
        System.setProperty("related-product.max.related.product.post.data.size.in.bytes","32");

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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.min.related.product.post.data.size.in.bytes","16");
        System.setProperty("related-product.max.related.product.post.data.size.in.bytes","32");

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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.min.related.product.post.data.size.in.bytes","16");
        System.setProperty("related-product.max.related.product.post.data.size.in.bytes","1024");

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
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        System.setProperty("related-product.size.of.incoming.request.queue","2");

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

        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
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
        return new TestBootstrapApplicationCtx(new TestRelatedProductStorageRepositoryFactory(latch));
    }

    public TestBootstrapApplicationCtx getSlowTestBootStrap(CountDownLatch latch) {
        return new TestBootstrapApplicationCtx(new SlowTestRelatedProductStorageRepositoryFactory(latch));
    }

    public class TestBootstrapApplicationCtx extends BootstrapApplicationCtx {

        private volatile TestRelatedProductStorageRepositoryFactory repository;

        public TestBootstrapApplicationCtx(TestRelatedProductStorageRepositoryFactory repositoryFactory) {
            this.repository = repositoryFactory;
        }

        public RelatedProductStorageRepositoryFactory getStorageRepositoryFactory(Configuration applicationConfiguration) {
            return repository;
        }

        public Configuration createConfiguration() {
            return new SystemPropertiesConfiguration();
        }

        public TestRelatedProductStorageRepositoryFactory getRepository() {
            return repository;
        }
    }

    public class TestRelatedProductStorageRepositoryFactory implements RelatedProductStorageRepositoryFactory {
        List<TestRelatedProductStorageRepository> repos = new CopyOnWriteArrayList<>();

        final CountDownLatch latch;

        public TestRelatedProductStorageRepositoryFactory(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public RelatedProductStorageRepository getRepository(Configuration configuration) {
            TestRelatedProductStorageRepository repo = createRepo();
            repos.add(repo);
            return repo;
        }

        public TestRelatedProductStorageRepository createRepo() {
            return new TestRelatedProductStorageRepository(latch);
        }

        public List<TestRelatedProductStorageRepository> getRepos() {
            return repos;
        }
    }

    public class TestRelatedProductStorageRepository implements RelatedProductStorageRepository {
        AtomicInteger productsRequestedToBeStored = new AtomicInteger(0);
        AtomicBoolean shutdown = new AtomicBoolean(false);

        final CountDownLatch latch;

        public TestRelatedProductStorageRepository(CountDownLatch latch)
        {
            this.latch = latch;
        }

        @Override
        public void store(RelatedProductStorageLocationMapper indexToMapper, List<RelatedProduct> relatedProducts) {
            productsRequestedToBeStored.addAndGet(relatedProducts.size());
            for(int i=0;i<relatedProducts.size();i++) latch.countDown();
        }

        @Override
        public void shutdown() {
            shutdown.set(true);
        }

        public int getProductsRequestedToBeStored() {
            return productsRequestedToBeStored.get();
        }

        public boolean isShutdown() {
            return shutdown.get();
        }
    }

    public class SlowTestRelatedProductStorageRepositoryFactory extends TestRelatedProductStorageRepositoryFactory {

        public SlowTestRelatedProductStorageRepositoryFactory(CountDownLatch latch) {
            super(latch);
        }

        public TestRelatedProductStorageRepository createRepo() {
            return new SlowTestRelatedProductStorageRepository(latch);
        }
    }

    public class SlowTestRelatedProductStorageRepository extends TestRelatedProductStorageRepository {
        AtomicInteger productsRequestedToBeStored = new AtomicInteger(0);
        AtomicBoolean shutdown = new AtomicBoolean(false);
        private final long sleepTime;

        public SlowTestRelatedProductStorageRepository(CountDownLatch latch)
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
        public void store(RelatedProductStorageLocationMapper indexToMapper, List<RelatedProduct> relatedProducts) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println("sleep interuptted");
                System.err.flush();
            }
            super.store(indexToMapper,relatedProducts);
        }


    }

}
