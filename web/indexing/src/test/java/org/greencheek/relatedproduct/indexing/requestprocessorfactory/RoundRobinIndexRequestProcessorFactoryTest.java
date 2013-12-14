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
    public void testRequestProcessorIsCalled() {
        System.setProperty("related-product.number.of.indexing.request.processors","2");
        Configuration configuration = new SystemPropertiesConfiguration();

        IndexingRequestConverterFactory requestBytesConverter = mock(IndexingRequestConverterFactory.class);
        when(requestBytesConverter.createConverter(any(Configuration.class),any(ByteBuffer.class))).thenReturn(new TestIndexingRequestConverter());

        RelatedProductIndexingMessageFactory indexingMessageFactory = new RelatedProductIndexingMessageFactory(configuration);
        TestRelatedProductIndexingMessageEventHandler indexingEventHandler = new TestRelatedProductIndexingMessageEventHandler();



        DisruptorIndexRequestProcessorFactory factory = new DisruptorIndexRequestProcessorFactory(requestBytesConverter,
                indexingMessageFactory,indexingEventHandler);

        RelatedProductIndexRequestProcessor processor = factory.createProcessor(configuration);


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
