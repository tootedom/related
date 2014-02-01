/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing.requestprocessors;

import com.lmax.disruptor.EventFactory;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessageFactory;
import org.greencheek.related.indexing.IndexingRequestConverterFactory;
import org.greencheek.related.indexing.jsonrequestprocessing.JsonSmartIndexingRequestConverterFactory;
import org.greencheek.related.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
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
public class DisruptorBasedRelatedItemIndexRequestProcessorTest {

    Configuration configuation;
    EventFactory<RelatedItemIndexingMessage> factory;
    IndexingRequestConverterFactory byteBufferToIndexingRequestMessageConverter;
    TestRelatedItemIndexingMessageEventHandler handler;

    DisruptorBasedRelatedItemIndexRequestProcessor processor;

    @Before
    public void setUp() {
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST, "1");
        configuation = new SystemPropertiesConfiguration();
        factory = new RelatedItemIndexingMessageFactory(configuation);
        byteBufferToIndexingRequestMessageConverter = new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());
        handler = new TestRelatedItemIndexingMessageEventHandler();

        processor = new DisruptorBasedRelatedItemIndexRequestProcessor(configuation,byteBufferToIndexingRequestMessageConverter,factory,handler);

    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST);
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
                        "    \"items\" : [ \"1\",\"2\",\"3\",\"4\",\"5\" ]"+
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
                        "    \"items\" : [ ]"+
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
                        "    \"items\" : [ \"1\" ]"+
                        "}";

        processor.processRequest(configuation, ByteBuffer.wrap(json.getBytes()));

        try {
            handler.getLatch().await(5000, TimeUnit.MILLISECONDS);
        } catch(Exception e) {
            fail("Failed waiting for disruptor to call the handler");
        }
        assertEquals(1,handler.getNumberOfCalls());
    }

    public class TestRelatedItemIndexingMessageEventHandler implements RelatedItemIndexingMessageEventHandler {

        final AtomicInteger numberOfCalls = new AtomicInteger(0);
        final AtomicBoolean shutdown = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void shutdown() {
            shutdown.set(true);
        }

        @Override
        public void onEvent(RelatedItemIndexingMessage event, long sequence, boolean endOfBatch) throws Exception {
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
