package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
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
    public void testSingleRequestProcessorIsCreated() {
        System.setProperty("related-product.number.of.indexing.request.processors","1");
        Configuration configuration = new SystemPropertiesConfiguration();

        IndexingRequestConverterFactory requestBytesConverter = mock(IndexingRequestConverterFactory.class);
        when(requestBytesConverter.createConverter(any(Configuration.class),any(ByteBuffer.class))).thenReturn(new TestIndexingRequestConverter());

        RelatedProductIndexingMessageFactory indexingMessageFactory = new RelatedProductIndexingMessageFactory(configuration);
        RelatedProductIndexingMessageEventHandler roundRobinIndexingEventHandler = mock(RelatedProductIndexingMessageEventHandler.class);
        RelatedProductIndexingMessageEventHandler singleIndexingEventHandler = mock(RelatedProductIndexingMessageEventHandler.class);

        final CountDownLatch latch = new CountDownLatch(1);

        try {
            doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        latch.countDown();
                        return null;
                    }
                }).when(singleIndexingEventHandler).onEvent(any(RelatedProductIndexingMessage.class),anyLong(),anyBoolean());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RoundRobinIndexRequestProcessorFactory factory = new RoundRobinIndexRequestProcessorFactory(requestBytesConverter,
                indexingMessageFactory,roundRobinIndexingEventHandler,singleIndexingEventHandler);

        RelatedProductIndexRequestProcessor processor = factory.createProcessor(configuration);


        processor.processRequest(configuration, ByteBuffer.wrap(new byte[0]));

        try {
            latch.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Message failed to be propogated around the Ring buffer.");
            e.printStackTrace();
        }

        try {
            verify(requestBytesConverter,times(1)).createConverter(any(Configuration.class),any(ByteBuffer.class));
            verify(singleIndexingEventHandler).onEvent(any(RelatedProductIndexingMessage.class),anyLong(),anyBoolean());
            verify(roundRobinIndexingEventHandler,times(1)).shutdown();
        } catch (Exception e){
            fail("No exception should have been throw");
        }

    }

    @Test
    public void testRoundRobinRequestProcessorIsCreated() {
        System.setProperty("related-product.number.of.indexing.request.processors","2");
        Configuration configuration = new SystemPropertiesConfiguration();

        IndexingRequestConverterFactory requestBytesConverter = mock(IndexingRequestConverterFactory.class);
        when(requestBytesConverter.createConverter(any(Configuration.class),any(ByteBuffer.class))).thenReturn(new TestIndexingRequestConverter());

        RelatedProductIndexingMessageFactory indexingMessageFactory = new RelatedProductIndexingMessageFactory(configuration);
        TestRelatedProductIndexingMessageEventHandler roundRobinIndexingEventHandler = new TestRelatedProductIndexingMessageEventHandler();
        TestRelatedProductIndexingMessageEventHandler singleIndexingEventHandler = new TestRelatedProductIndexingMessageEventHandler();



        RoundRobinIndexRequestProcessorFactory factory = new RoundRobinIndexRequestProcessorFactory(requestBytesConverter,
                indexingMessageFactory,roundRobinIndexingEventHandler,singleIndexingEventHandler);

        RelatedProductIndexRequestProcessor processor = factory.createProcessor(configuration);


        processor.processRequest(configuration, ByteBuffer.wrap(new byte[0]));

        try {
            try {
                roundRobinIndexingEventHandler.getLatch().await(5000,TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fail("Message failed to be propogated around the Ring buffer, to the round robin indexing handler");
            }


            verify(requestBytesConverter,times(1)).createConverter(any(Configuration.class), any(ByteBuffer.class));

            assertEquals(1, singleIndexingEventHandler.getLatch().getCount());
            assertTrue(singleIndexingEventHandler.isShutdown());
        } catch (Exception e){
            fail("No exception should have been throw");
        }

    }

    private class TestIndexingRequestConverter implements IndexingRequestConverter {

        @Override
        public void translateTo(RelatedProductIndexingMessage convertedTo, long sequence) {
            convertedTo.setValidMessage(true);
            convertedTo.setUTCFormattedDate("2012-08-09T10:20:20+0000");
            convertedTo.getIndexingMessageProperties().addProperty("site","tkmax");
            convertedTo.getRelatedProducts().setNumberOfRelatedProducts(1);
            convertedTo.getRelatedProducts().getRelatedProductAtIndex(0).setId("1");
            convertedTo.getRelatedProducts().getRelatedProductAtIndex(0).getAdditionalProperties().addProperty("type","fish");
        }
    }

    private class TestRelatedProductIndexingMessageEventHandler implements RelatedProductIndexingMessageEventHandler {

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
        public void onEvent(RelatedProductIndexingMessage event, long sequence, boolean endOfBatch) throws Exception {
            latch.countDown();
        }

        public CountDownLatch getLatch() {
            return latch;
        }
    }
}
