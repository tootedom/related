package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Ring buffer that batches up the user search requests as RelatedProductSearch objects.
 * These are then handled by the {@link RelatedProductSearchDisruptorEventHandler}
 */
public class DisruptorBasedRelatedProductSearchExecutor implements RelatedProductSearchExecutor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductSearchExecutor.class);


    private final ExecutorService executorService;
    private final Disruptor<RelatedProductSearch> disruptor;

    private final Configuration configuration;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final RelatedProductSearchDisruptorEventHandler eventHandler;


    public DisruptorBasedRelatedProductSearchExecutor(final Configuration configuration,
                                                      EventFactory<RelatedProductSearch> eventFactory,
                                                      RelatedProductSearchDisruptorEventHandler eventHandler
    ) {
        this.configuration = configuration;
        this.eventHandler = eventHandler;

        this.executorService = getExecutorService();
        disruptor = new Disruptor<RelatedProductSearch>(
                eventFactory,
                configuration.getSizeOfRelatedContentSearchRequestHandlerQueue(), executorService,
                ProducerType.SINGLE, configuration.getWaitStrategyFactory().createWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("SearchExecutor"));
    }


    @Override
    public void executeSearch(RelatedProductSearch searchRequest) {
        if(!shutdown.get()) {
            disruptor.publishEvent(RelatedProductSearchTranslator.INSTANCE,searchRequest);
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
