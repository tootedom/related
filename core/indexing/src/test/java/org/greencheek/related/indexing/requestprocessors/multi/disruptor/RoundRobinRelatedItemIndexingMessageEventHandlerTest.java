package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import org.greencheek.related.api.indexing.BasicRelatedItemIndexingMessageConverter;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.api.indexing.RelatedItemReference;
import org.greencheek.related.api.indexing.RelatedItemReferenceMessageFactory;
import org.greencheek.related.indexing.requestprocessors.RelatedItemIndexingMessageEventHandler;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
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
public class RoundRobinRelatedItemIndexingMessageEventHandlerTest {

    private static final String DATE = "2013-05-22T20:31:35";
    private static final String PRODUCTID_1 = "1";
    private static final String PRODUCTID_2 = "2";
    private static final String PRODUCTID_3 = "3";

    private static final String SITE = "amazon";
    private static final String CHANNEL = "uk";
    private static final String DEPARTMENT = "electronics";
    private static final String SUBCATEGORY = "accessories";

    RelatedItemIndexingMessageEventHandler handler;
    Configuration configuration;
    TestRelatedItemReferenceEventHandlerFactory repo;

    @Before
    public void setUp() {
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM,"10");
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE, "25");
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS,"2");
        configuration = new SystemPropertiesConfiguration();
        repo = new TestRelatedItemReferenceEventHandlerFactory();
        handler = new RoundRobinRelatedItemIndexingMessageEventHandler(configuration,new BasicRelatedItemIndexingMessageConverter(configuration),
                new RelatedItemReferenceMessageFactory(),repo);
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM);
        System.clearProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE);
        System.clearProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_INDEXING_REQUEST_PROCESSORS);
        handler.shutdown();
    }


    private RelatedItemIndexingMessage getMessage() {
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(configuration);

        message.setValidMessage(true);
        message.setUTCFormattedDate(DATE);
        message.getIndexingMessageProperties().addProperty("site", SITE);
        message.getIndexingMessageProperties().addProperty("channel",CHANNEL);
        message.getRelatedItems().setNumberOfRelatedItems(3);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).setId(PRODUCTID_1);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("subcategory","laptops");
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("name","apple mac");

        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).setId(PRODUCTID_2);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("name","apple care insurance");

        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).setId(PRODUCTID_3);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).additionalProperties.addProperty("name","microsoft word");

        return message;
    }

    @Test
    public void testSendingOneItem() {
        CountDownLatch latch = new CountDownLatch(3);
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
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(configuration);
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
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(configuration);
        message.setValidMessage(true);
        message.getRelatedItems().setNumberOfRelatedItems(0);
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

    private class TestRelatedItemReferenceEventHandlerFactory implements RelatedItemReferenceEventHandlerFactory {

        public List<TestRelatedItemReferenceEventHandler> handlers = new ArrayList<>();

        @Override
        public RelatedItemReferenceEventHandler getHandler() {
            TestRelatedItemReferenceEventHandler handler = new TestRelatedItemReferenceEventHandler();
            handlers.add(handler);
            return handler;
        }
    }

    private class TestRelatedItemReferenceEventHandler implements RelatedItemReferenceEventHandler {

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
        public void onEvent(RelatedItemReference event, long sequence, boolean endOfBatch) throws Exception {
            calls.incrementAndGet();
            endOfBatchCountDowns.countDown();
            if(endOfBatch) {
                endOfBatchCalls.incrementAndGet();
            }
        }
    }
}
