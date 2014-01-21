package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.api.indexing.RelatedItemReference;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.locationmappers.DayBasedStorageLocationMapper;
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
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 11/12/2013
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public class BatchingRelatedItemReferenceEventHandlerTest {

    BatchingRelatedItemReferenceEventHandler handler;
    Configuration configuration;
    TestRelatedItemStorageRepository repo;

    @Before
    public void setUp() {
        System.setProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE, "25");
        configuration = new SystemPropertiesConfiguration();
        repo = new TestRelatedItemStorageRepository();
        handler = new BatchingRelatedItemReferenceEventHandler(configuration.getIndexBatchSize(),repo,
                new DayBasedStorageLocationMapper(configuration,new JodaUTCCurrentDateFormatter()));
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_BATCH_INDEX_SIZE);
        handler.shutdown();
    }

    public RelatedItemReference getMessage() {
        RelatedItemReference ref = new RelatedItemReference();
        ref.setReference(new RelatedItem("1".toCharArray(),"2012-09-12T12:12:12+01:00",null,null));
        return ref;
    }

    @Test
    public void testSendingOneItem() {
        try {
            handler.onEvent(getMessage(),1,true);
        } catch(Exception e) {
            fail();
        }

        assertEquals(1,repo.getNumberOfCalls());
        assertEquals(1,repo.getNumberOfProductsSentForStoring());
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
        assertEquals(8,repo.getNumberOfProductsSentForStoring());
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

        assertEquals(2,repo.getNumberOfCalls());
        assertEquals(26,repo.getNumberOfProductsSentForStoring());
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
