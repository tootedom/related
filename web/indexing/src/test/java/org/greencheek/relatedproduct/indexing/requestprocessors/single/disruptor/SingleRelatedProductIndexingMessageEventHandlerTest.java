package org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor;

import org.greencheek.relatedproduct.api.indexing.BasicRelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.locationmappers.DayBasedStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.indexing.util.JodaUTCCurrentDateFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
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
public class SingleRelatedProductIndexingMessageEventHandlerTest {

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
    TestRelatedProductStorageRepository repo;

    @Before
    public void setUp() {
        System.setProperty("related-product.index.batch.size", "25");
        configuration = new SystemPropertiesConfiguration();
        repo = new TestRelatedProductStorageRepository();
        handler = new SingleRelatedProductIndexingMessageEventHandler(configuration,new BasicRelatedProductIndexingMessageConverter(configuration),
                repo,new DayBasedStorageLocationMapper(configuration,new JodaUTCCurrentDateFormatter()));
    }

    @After
    public void tearDown() {
        System.clearProperty("related-product.index.batch.size");
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
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(configuration);
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
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(configuration);
        message.setValidMessage(true);
        message.getRelatedProducts().setNumberOfRelatedProducts(0);
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

    private class TestRelatedProductStorageRepository implements RelatedProductStorageRepository{

        AtomicInteger calls = new AtomicInteger(0);
        AtomicInteger numberOfProducts = new AtomicInteger(0);
        AtomicBoolean shutdownCalled = new AtomicBoolean(false);



        @Override
        public void store(RelatedProductStorageLocationMapper indexToMapper, List<RelatedProduct> relatedProducts) {
            numberOfProducts.getAndAdd(relatedProducts.size());
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
