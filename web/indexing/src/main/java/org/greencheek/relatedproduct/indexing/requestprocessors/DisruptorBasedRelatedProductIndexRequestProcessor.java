package org.greencheek.relatedproduct.indexing.requestprocessors;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.*;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class DisruptorBasedRelatedProductIndexRequestProcessor implements RelatedProductIndexRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductIndexRequestProcessor.class);

    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductIndexingMessage> disruptor;
    private final RingBuffer<RelatedProductIndexingMessage> ringBuffer;

    private final IndexingRequestConverterFactory requestConverter;
    private final RelatedProductIndexingMessageEventHandler eventHandler;

    private volatile boolean canIndex = true;

    private final boolean canOutputRequestData;

    public DisruptorBasedRelatedProductIndexRequestProcessor(Configuration configuration,
                                                             IndexingRequestConverterFactory requestConverter,
                                                             EventFactory<RelatedProductIndexingMessage> indexingMessageFactory,
                                                             RelatedProductIndexingMessageEventHandler eventHandler
    ) {

        this.canOutputRequestData = configuration.isSafeToOutputRequestData();
        this.requestConverter = requestConverter;
        disruptor = new Disruptor<RelatedProductIndexingMessage>(
                indexingMessageFactory,
                Util.ceilingNextPowerOfTwo(configuration.getSizeOfIncomingMessageQueue()), executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());

        this.eventHandler = eventHandler;
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        ringBuffer = disruptor.start();

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
