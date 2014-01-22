package org.greencheek.related.indexing.requestprocessors.single.disruptor;

import org.greencheek.related.api.indexing.BasicRelatedItemIndexingMessageConverter;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.related.indexing.requestprocessors.RelatedItemIndexingMessageEventHandler;
import org.greencheek.related.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests batching effect of the indexing message event handler
 */
public class SingleRelatedItemIndexingMessageEventHandlerTest {

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
    TestRelatedItemStorageRepository repo;

    @Before
    public void setUp() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE, "25");
        configuration = new SystemPropertiesConfiguration();
        repo = new TestRelatedItemStorageRepository();
        handler = new SingleRelatedItemIndexingMessageEventHandler(configuration,new BasicRelatedItemIndexingMessageConverter(configuration),
                repo,new DayBasedStorageLocationMapper(configuration,new JodaUTCCurrentDateFormatter()));
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE);
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
        try {
            handler.onEvent(getMessage(),1,true);
        } catch(Exception e) {
            fail();
        }

        assertEquals(1,repo.getNumberOfCalls());
        assertEquals(3,repo.getNumberOfProductsSentForStoring());
    }

    @Test
    public void testSendingManyItemsButBelowBatchSize() {
        try {
            for(int i=0;i<7;i++) {
                handler.onEvent(getMessage(),i,false);
            }
            handler.onEvent(getMessage(),8,true);
        } catch(Exception e) {
            fail();
        }

        assertEquals(1,repo.getNumberOfCalls());
        assertEquals(24,repo.getNumberOfProductsSentForStoring());
    }

    @Test
    public void testSendingManyItemsExceedingBatchSize() {
        try {
            for(int i=0;i<25;i++) {
                handler.onEvent(getMessage(),i,false);
            }
            handler.onEvent(getMessage(),26,true);

        } catch(Exception e) {
            fail();
        }

        assertEquals(3,repo.getNumberOfCalls());
        assertEquals(78,repo.getNumberOfProductsSentForStoring());
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

        assertEquals(0,repo.getNumberOfCalls());
        assertEquals(0,repo.getNumberOfProductsSentForStoring());
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

        assertEquals(0,repo.getNumberOfCalls());
        assertEquals(0,repo.getNumberOfProductsSentForStoring());
    }

    @Test
    public void testShutdownIsCalledOnStorageRepo() {
        handler.shutdown();

        assertTrue(repo.isShutdown());
    }

    private class TestRelatedItemStorageRepository implements RelatedItemStorageRepository {

        AtomicInteger calls = new AtomicInteger(0);
        AtomicInteger numberOfProducts = new AtomicInteger(0);
        AtomicBoolean shutdownCalled = new AtomicBoolean(false);



        @Override
        public void store(RelatedItemStorageLocationMapper indexToMapper, List<RelatedItem> relatedItems) {
            numberOfProducts.getAndAdd(relatedItems.size());
            calls.incrementAndGet();
        }

        @Override
        public void shutdown() {
            shutdownCalled.set(true);
        }

        public int getNumberOfCalls() {
            return calls.get();
        }

        public int getNumberOfProductsSentForStoring() {
            return numberOfProducts.get();
        }

        public boolean isShutdown() {
            return shutdownCalled.get();
        }
    }
}
