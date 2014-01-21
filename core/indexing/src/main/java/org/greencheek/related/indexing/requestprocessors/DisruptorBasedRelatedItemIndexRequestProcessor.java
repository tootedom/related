package org.greencheek.related.indexing.requestprocessors;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.indexing.*;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.arrayindexing.Util;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class DisruptorBasedRelatedItemIndexRequestProcessor implements RelatedItemIndexRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedItemIndexRequestProcessor.class);

    private final ExecutorService executorService;
    private final Disruptor<RelatedItemIndexingMessage> disruptor;
    private final RingBuffer<RelatedItemIndexingMessage> ringBuffer;

    private final IndexingRequestConverterFactory requestConverter;
    private final RelatedItemIndexingMessageEventHandler eventHandler;

    private volatile boolean canIndex = true;

    private final boolean canOutputRequestData;

    public DisruptorBasedRelatedItemIndexRequestProcessor(Configuration configuration,
                                                          IndexingRequestConverterFactory requestConverter,
                                                          EventFactory<RelatedItemIndexingMessage> indexingMessageFactory,
                                                          RelatedItemIndexingMessageEventHandler eventHandler
    ) {
        this.executorService = getExecutorService();
        this.canOutputRequestData = configuration.isSafeToOutputRequestData();
        this.requestConverter = requestConverter;
        disruptor = new Disruptor<RelatedItemIndexingMessage>(
                indexingMessageFactory,
                Util.ceilingNextPowerOfTwo(configuration.getSizeOfIncomingMessageQueue()), executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());

        this.eventHandler = eventHandler;
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        ringBuffer = disruptor.start();

    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("IndexRequestProcessor"));
    }

    @Override
    public IndexRequestPublishingStatus processRequest(Configuration config, ByteBuffer data) {
        if(canIndex) {
            try {
                boolean published = ringBuffer.tryPublishEvent(requestConverter.createConverter(config,data));
                return published ? IndexRequestPublishingStatus.PUBLISHED : IndexRequestPublishingStatus.NO_SPACE_AVALIABLE;
            } catch(InvalidIndexingRequestException e) {
                log.warn("Invalid json content, unable to process request: {}", e.getMessage());

                if(log.isDebugEnabled()) {
                    if(canOutputRequestData && data.hasArray()) {
                        log.debug("content requested to be indexed: {}", Arrays.toString(data.array()));
                    }
                }

                return IndexRequestPublishingStatus.FAILED;
            }
        } else {
            log.error("The indexing processor has been shutdown, and cannot accept requests.");
            return IndexRequestPublishingStatus.PUBLISHER_SHUTDOWN;
        }
    }


    @PreDestroy
    public void shutdown() {
        canIndex = false;

        try {
            log.info("Attempting to shut down executor thread pool in index request processor");
            executorService.shutdownNow();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in index request processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in index request processor");
            disruptor.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in index request processor",e);
        }

        log.info("Shutting down round robin index request event handler");
        eventHandler.shutdown();


    }


}
