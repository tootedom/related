package org.greencheek.related.indexing.requestprocessorfactory;

import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessageFactory;
import org.greencheek.related.indexing.IndexingRequestConverter;
import org.greencheek.related.indexing.IndexingRequestConverterFactory;
import org.greencheek.related.indexing.RelatedItemIndexRequestProcessor;
import org.greencheek.related.indexing.requestprocessors.RelatedItemIndexingMessageEventHandler;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 *
 */
public class RoundRobinIndexRequestProcessorFactoryTest {



    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        System.clearProperty("related-product.number.of.indexing.request.processors");
    }

    @Test
    public void testRequestProcessorIsCalled() {
        System.setProperty("related-product.number.of.indexing.request.processors","2");
        Configuration configuration = new SystemPropertiesConfiguration();

        IndexingRequestConverterFactory requestBytesConverter = mock(IndexingRequestConverterFactory.class);
        when(requestBytesConverter.createConverter(any(Configuration.class),any(ByteBuffer.class))).thenReturn(new TestIndexingRequestConverter());

        RelatedItemIndexingMessageFactory indexingMessageFactory = new RelatedItemIndexingMessageFactory(configuration);
        TestRelatedItemIndexingMessageEventHandler indexingEventHandler = new TestRelatedItemIndexingMessageEventHandler();



        DisruptorIndexRequestProcessorFactory factory = new DisruptorIndexRequestProcessorFactory(requestBytesConverter,
                indexingMessageFactory,indexingEventHandler);

        RelatedItemIndexRequestProcessor processor = factory.createProcessor(configuration);


        processor.processRequest(configuration, ByteBuffer.wrap(new byte[0]));

        try {
            try {
                indexingEventHandler.getLatch().await(5000,TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fail("Message failed to be propogated around the Ring buffer, to the round robin indexing handler");
            }


            verify(requestBytesConverter,times(1)).createConverter(any(Configuration.class), any(ByteBuffer.class));


        } catch (Exception e){
            fail("No exception should have been throw");
        }

    }

    private class TestIndexingRequestConverter implements IndexingRequestConverter {

        @Override
        public void translateTo(RelatedItemIndexingMessage convertedTo, long sequence) {
            convertedTo.setValidMessage(true);
            convertedTo.setUTCFormattedDate("2012-08-09T10:20:20+0000");
            convertedTo.getIndexingMessageProperties().addProperty("site","tkmax");
            convertedTo.getRelatedItems().setNumberOfRelatedItems(1);
            convertedTo.getRelatedItems().getRelatedItemAtIndex(0).setId("1");
            convertedTo.getRelatedItems().getRelatedItemAtIndex(0).getAdditionalProperties().addProperty("type","fish");
        }
    }

    private class TestRelatedItemIndexingMessageEventHandler implements RelatedItemIndexingMessageEventHandler {

        final AtomicBoolean shutdown = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        @Override
        public void shutdown() {
            shutdown.set(true);
        }

        public boolean isShutdown() {
            return shutdown.get();
        }

        @Override
        public void onEvent(RelatedItemIndexingMessage event, long sequence, boolean endOfBatch) throws Exception {
            latch.countDown();
        }

        public CountDownLatch getLatch() {
            return latch;
        }
    }
}
