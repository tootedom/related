package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import org.greencheek.relatedproduct.api.indexing.BasicRelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReferenceMessageFactory;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReference;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 11/12/2013
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinRelatedProductIndexingMessageEventHandlerTest {

    private static final String DATE = "2013-05-22T20:31:35";
    private static final String PRODUCTID_1 = "1";
    private static final String PRODUCTID_2 = "2";
    private static final String PRODUCTID_3 = "3";

    private static final String SITE = "amazon";
    private static final String CHANNEL = "uk";
    private static final String DEPARTMENT = "electronics";
    private static final String SUBCATEGORY = "accessories";

    RelatedProductIndexingMessageEventHandler handler;
    Configuration configuration;
    TestRelatedProductReferenceEventHandlerFactory repo;

    @Before
    public void setUp() {
        System.setProperty("related-product.max.number.related.products.per.product","10");
        System.setProperty("related-product.index.batch.size", "25");
        System.setProperty("related-product.number.of.indexing.request.processors","2");
        configuration = new SystemPropertiesConfiguration();
        repo = new TestRelatedProductReferenceEventHandlerFactory();
        handler = new RoundRobinRelatedProductIndexingMessageEventHandler(configuration,new BasicRelatedProductIndexingMessageConverter(configuration),
                new RelatedProductReferenceMessageFactory(),repo);
    }

    @After
    public void tearDown() {
        System.clearProperty("related-product.max.number.related.products.per.product");
        System.clearProperty("related-product.index.batch.size");
        System.clearProperty("related-product.number.of.indexing.request.processors");
        handler.shutdown();
    }


    private RelatedProductIndexingMessage getMessage() {
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(configuration);

        message.setValidMessage(true);
        message.setUTCFormattedDate(DATE);
        message.getIndexingMessageProperties().addProperty("site", SITE);
        message.getIndexingMessageProperties().addProperty("channel",CHANNEL);
        message.getRelatedProducts().setNumberOfRelatedProducts(3);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(0).setId(PRODUCTID_1);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(0).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(0).additionalProperties.addProperty("subcategory","laptops");
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(0).additionalProperties.addProperty("name","apple mac");

        message.getRelatedProducts().getCheckedRelatedProductAtIndex(1).setId(PRODUCTID_2);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(1).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(1).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(1).additionalProperties.addProperty("name","apple care insurance");

        message.getRelatedProducts().getCheckedRelatedProductAtIndex(2).setId(PRODUCTID_3);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(2).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(2).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedProducts().getCheckedRelatedProductAtIndex(2).additionalProperties.addProperty("name","microsoft word");

        return message;
    }

    @Test
    public void testSendingOneItem() {
        CountDownLatch latch = new CountDownLatch(1);
        repo.handlers.get(0).setEndOfBatchCountDownLatch(latch);
        repo.handlers.get(1).setEndOfBatchCountDownLatch(latch);
        try {
            handler.onEvent(getMessage(),1,true);
        } catch(Exception e) {
            fail();
        }
        try {
            boolean countedDown = latch.await(5000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);

        } catch (InterruptedException e) {
            fail("Timed out waiting for messages to be sent through the ring buffer");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertEquals(3,repo.handlers.get(0).getNumberOfCalls());
        assertEquals(1,repo.handlers.get(0).getNumberOfEndOfBatchCalls());

    }

    @Test
    public void testSendingManyItemsButBelowBatchSize() {
        CountDownLatch latch = new CountDownLatch(24);
        repo.handlers.get(0).setEndOfBatchCountDownLatch(latch);
        repo.handlers.get(1).setEndOfBatchCountDownLatch(latch);

        try {
            for(int i=0;i<7;i++) {
                handler.onEvent(getMessage(),i,false);
            }
            handler.onEvent(getMessage(),8,true);
        } catch(Exception e) {
            fail();
        }
        try {
            boolean countedDown = latch.await(10000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);

        } catch (InterruptedException e) {
            fail("Timed out waiting for messages to be sent through the ring buffer");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        assertEquals(24,repo.handlers.get(0).getNumberOfCalls());
        assertEquals(1,repo.handlers.get(0).getNumberOfEndOfBatchCalls());

    }

    @Test
    public void testSendingManyItemsExceedingBatchSize() {
        CountDownLatch latch = new CountDownLatch(78);
        repo.handlers.get(0).setEndOfBatchCountDownLatch(latch);
        repo.handlers.get(1).setEndOfBatchCountDownLatch(latch);
        try {
            for(int i=0;i<25;i++) {
                handler.onEvent(getMessage(),i,false);
            }
            handler.onEvent(getMessage(),26,true);

        } catch(Exception e) {
            fail();
        }

        try {
            boolean countedDown = latch.await(10000, TimeUnit.MILLISECONDS);
            assertTrue("Storage Repository not called in required time",countedDown);

        } catch (InterruptedException e) {
            fail("Timed out waiting for messages to be sent through the ring buffer");
            e.printStackTrace();
        }


        assertEquals(51,repo.handlers.get(0).getNumberOfCalls());
        assertTrue(repo.handlers.get(0).getNumberOfEndOfBatchCalls()>0);

        assertEquals(27,repo.handlers.get(1).getNumberOfCalls());
        assertTrue(repo.handlers.get(1).getNumberOfEndOfBatchCalls()>0);
    }



    @Test
    public void checkNoCallMadeForInvalidMessage() {
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(configuration);
        message.setValidMessage(false);
        try {
            handler.onEvent(message,1,true);
        } catch (Exception e) {
            fail();
        }

        assertEquals(0,repo.handlers.get(0).getNumberOfCalls());
    }

    @Test
    public void checkNoCallMadeForMessageWithNoProducts() {
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(configuration);
        message.setValidMessage(true);
        message.getRelatedProducts().setNumberOfRelatedProducts(0);
        try {
            handler.onEvent(message,1,true);
        } catch (Exception e) {
            fail();
        }

        assertEquals(0,repo.handlers.get(0).getNumberOfCalls());
    }

    @Test
    public void testShutdownIsCalledOnStorageRepo() {
        handler.shutdown();

        assertTrue(repo.handlers.get(0).isShutdown());
        assertTrue(repo.handlers.get(1).isShutdown());
    }

    private class TestRelatedProductReferenceEventHandlerFactory implements RelatedProductReferenceEventHandlerFactory {

        public List<TestRelatedProductReferenceEventHandler> handlers = new ArrayList<>();

        @Override
        public RelatedProductReferenceEventHandler getHandler() {
            TestRelatedProductReferenceEventHandler handler = new TestRelatedProductReferenceEventHandler();
            handlers.add(handler);
            return handler;
        }
    }

    private class TestRelatedProductReferenceEventHandler implements RelatedProductReferenceEventHandler {

        AtomicInteger endOfBatchCalls = new AtomicInteger(0);
        AtomicInteger calls = new AtomicInteger(0);
        AtomicBoolean shutdownCalled = new AtomicBoolean(false);
        volatile CountDownLatch endOfBatchCountDowns = new CountDownLatch(1);


        @Override
        public void shutdown() {
            shutdownCalled.set(true);
        }

        public int getNumberOfCalls() {
            return calls.get();
        }

        public int getNumberOfEndOfBatchCalls() {
            return endOfBatchCalls.get();
        }

        public void setEndOfBatchCountDownLatch(CountDownLatch latch) {
            endOfBatchCountDowns = latch;
        }


        public boolean isShutdown() {
            return shutdownCalled.get();
        }

        @Override
        public void onEvent(RelatedProductReference event, long sequence, boolean endOfBatch) throws Exception {
            calls.incrementAndGet();
            endOfBatchCountDowns.countDown();
            if(endOfBatch) {
                endOfBatchCalls.incrementAndGet();
            }
        }
    }
}
