package org.greencheek.related.searching.disruptor.searchexecution;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.util.arrayindexing.Util;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Ring buffer that batches up the user search requests as RelatedItemSearch objects.
 * These are then handled by the {@link RelatedItemSearchDisruptorEventHandler}
 */
public class DisruptorBasedRelatedItemSearchExecutor implements RelatedItemSearchExecutor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedItemSearchExecutor.class);


    private final ExecutorService executorService;
    private final Disruptor<RelatedItemSearch> disruptor;

    private final Configuration configuration;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final RelatedItemSearchDisruptorEventHandler eventHandler;


    public DisruptorBasedRelatedItemSearchExecutor(final Configuration configuration,
                                                   EventFactory<RelatedItemSearch> eventFactory,
                                                   RelatedItemSearchDisruptorEventHandler eventHandler
    ) {
        this.configuration = configuration;
        this.eventHandler = eventHandler;
        int bufferSize = configuration.getSizeOfRelatedItemSearchRequestHandlerQueue();

        this.executorService = getExecutorService();
        disruptor = new Disruptor<RelatedItemSearch>(
                eventFactory,bufferSize, executorService,
                ProducerType.SINGLE, configuration.getWaitStrategyFactory().createWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("SearchExecutor"));
    }


    @Override
    public void executeSearch(RelatedItemSearch searchRequest) {
        if(!shutdown.get()) {
            disruptor.publishEvent(RelatedItemSearchTranslator.INSTANCE,searchRequest);
        } else {
            log.warn("Unable to publish events, as the search executor has been shutdown");
        }
    }

    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {

            log.info("Shutting down index request processor");
            try {
                log.info("Attempting to shut down disruptor in search handler processor");
                disruptor.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down disruptor in search handler processor",e);
            }

            try {
                log.info("Attempting to shutdown the search event handler");
                eventHandler.shutdown();
            } catch (Exception e) {
                log.warn("Unable to/Exception during shutdown of the search event handler");
            }

            try {
                log.info("Attempting to shut down executor thread pool in search handler processor");
                executorService.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down executor thread pool in search handler processor",e);
            }
        }
    }
}
