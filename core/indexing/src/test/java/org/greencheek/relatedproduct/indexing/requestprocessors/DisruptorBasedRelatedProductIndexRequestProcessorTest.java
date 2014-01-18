package org.greencheek.relatedproduct.indexing.requestprocessors;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.jsonrequestprocessing.JsonSmartIndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class DisruptorBasedRelatedProductIndexRequestProcessorTest {

    Configuration configuation;
    EventFactory<RelatedProductIndexingMessage> factory;
    IndexingRequestConverterFactory byteBufferToIndexingRequestMessageConverter;
    TestRelatedProductIndexingMessageEventHandler handler;

    DisruptorBasedRelatedProductIndexRequestProcessor processor;

    @Before
    public void setUp() {
        System.setProperty("related-product.max.number.related.products.per.product", "1");
        configuation = new SystemPropertiesConfiguration();
        factory = new RelatedProductIndexingMessageFactory(configuation);
        byteBufferToIndexingRequestMessageConverter = new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());
        handler = new TestRelatedProductIndexingMessageEventHandler();

        processor = new DisruptorBasedRelatedProductIndexRequestProcessor(configuation,byteBufferToIndexingRequestMessageConverter,factory,handler);

    }

    @After
    public void tearDown() {
        System.clearProperty("related-product.max.number.related.products.per.product");
        processor.shutdown();
    }

    @Test
    public void testHandlerIsNotExecutedWithEmptyRequestData() {
        processor.processRequest(configuation, ByteBuffer.wrap(new byte[0]));

        assertEquals(0,handler.getNumberOfCalls());
    }

    @Test
    public void testHandlerIsNotExecutedWithIndexingRequestWithTooManyProducts() {
        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : [ \"1\",\"2\",\"3\",\"4\",\"5\" ]"+
                        "}";

        processor.processRequest(configuation, ByteBuffer.wrap(json.getBytes()));

        assertEquals(0,handler.getNumberOfCalls());
    }

    @Test
    public void testHandlerIsNotExecutedWithIndexingRequestWithNoProducts() {
        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : [ ]"+
                        "}";

        processor.processRequest(configuation, ByteBuffer.wrap(json.getBytes()));

        assertEquals(0,handler.getNumberOfCalls());
    }

    @Test
    public void testHandlerIsCalled() {
        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : [ \"1\" ]"+
                        "}";

        processor.processRequest(configuation, ByteBuffer.wrap(json.getBytes()));

        try {
            handler.getLatch().await(5000, TimeUnit.MILLISECONDS);
        } catch(Exception e) {
            fail("Failed waiting for disruptor to call the handler");
        }
        assertEquals(1,handler.getNumberOfCalls());
    }

    public class TestRelatedProductIndexingMessageEventHandler implements RelatedProductIndexingMessageEventHandler {

        final AtomicInteger numberOfCalls = new AtomicInteger(0);
        final AtomicBoolean shutdown = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void shutdown() {
            shutdown.set(true);
        }

        @Override
        public void onEvent(RelatedProductIndexingMessage event, long sequence, boolean endOfBatch) throws Exception {
            numberOfCalls.incrementAndGet();
        }

        public int getNumberOfCalls() {
            return numberOfCalls.get();
        }

        public CountDownLatch getLatch() {
            return latch;
        }
    }
}
