package org.greencheek.relatedproduct.indexing.web;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.indexing.bootstrap.BootstrapApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
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
        shutdownTomcat();

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

    @Test
    public void testIndexingSingleItemWithSingleRequestProcessor() {
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","1");

        final CountDownLatch latch = new CountDownLatch(1);
        TestBootstrapApplicationCtx bootstrap = new TestBootstrapApplicationCtx(latch);
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
            latch.await(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }



        int i = 0;
        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
        }

        assertEquals(3,i);
    }

    @Test
    public void testIndexingSingleItemWithMultipleRequestProcessor() {
        System.setProperty("related-product.index.batch.size","3");
        System.setProperty("related-product.number.of.indexing.request.processors","2");

        final CountDownLatch latch = new CountDownLatch(5);
        TestBootstrapApplicationCtx bootstrap = new TestBootstrapApplicationCtx(latch);
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
            latch.await(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            fail("Storage Repository not called in required time");
        }



        int i = 0;
        for(TestRelatedProductStorageRepository repo : bootstrap.getRepository().getRepos()) {
            i+= repo.getProductsRequestedToBeStored();
        }

        assertEquals(15,i);
    }

    private Response sendPost() {


        Response response=null;
        try {
            response = asyncHttpClient.preparePost(indexingurl).setBody(
                    "{\n" +
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
                            "}        "
            ).execute().get();

            assertEquals(202, response.getStatusCode());
        } catch (Exception e ) {
            fail(e.getMessage());
        } finally {
            return response;
        }
    }

    protected int getTomcatPort() {
        return tomcat.getConnector().getLocalPort();
    }



    public class TestBootstrapApplicationCtx extends BootstrapApplicationCtx {

        private final CountDownLatch latch;
        private volatile TestRelatedProductStorageRepositoryFactory repository;

        public TestBootstrapApplicationCtx(CountDownLatch latch) {
            this.latch = latch;
        }

        public RelatedProductStorageRepositoryFactory getStorageRepositoryFactory(Configuration applicationConfiguration) {
            repository = new TestRelatedProductStorageRepositoryFactory(latch);
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

        private final CountDownLatch latch;

        public TestRelatedProductStorageRepositoryFactory(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public RelatedProductStorageRepository getRepository(Configuration configuration) {
            TestRelatedProductStorageRepository repo = new TestRelatedProductStorageRepository(latch);
            repos.add(repo);
            return repo;
        }

        public List<TestRelatedProductStorageRepository> getRepos() {
            return repos;
        }
    }

    public class TestRelatedProductStorageRepository implements RelatedProductStorageRepository {
        AtomicInteger productsRequestedToBeStored = new AtomicInteger(0);
        AtomicBoolean shutdown = new AtomicBoolean(false);

        private final CountDownLatch latch;

        public TestRelatedProductStorageRepository(CountDownLatch latch)
        {
            this.latch = latch;
        }

        @Override
        public void store(RelatedProductStorageLocationMapper indexToMapper, List<RelatedProduct> relatedProducts) {
            productsRequestedToBeStored.addAndGet(relatedProducts.size());
            latch.countDown();
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

}
