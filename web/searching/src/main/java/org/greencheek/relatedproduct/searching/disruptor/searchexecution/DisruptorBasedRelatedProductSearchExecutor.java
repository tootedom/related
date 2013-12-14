package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedRelatedProductSearchExecutor implements RelatedProductSearchExecutor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductSearchExecutor.class);


    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductSearch> disruptor;

    private final Configuration configuration;

//    private final EventHandler<RelatedProductSearch> eventHandler;


    public DisruptorBasedRelatedProductSearchExecutor(final Configuration configuration,EventFactory<RelatedProductSearch> eventFactory,
                                                      RelatedProductSearchDisruptorEventHandler eventHandler
    ) {
        this.configuration = configuration;
//        this.eventHandler = eventHandler;

        disruptor = new Disruptor<RelatedProductSearch>(
                eventFactory,
                configuration.getSizeOfRelatedContentSearchRequestHandlerQueue(), executorService,
                ProducerType.SINGLE, new SleepingWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }



    @PreDestroy
    public void shutdown() {

        try {
            log.info("Attempting to shut down executor thread pool in search handler processor");
            executorService.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in search handler processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in search handler processor");
            disruptor.halt();
            disruptor.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in search handler processor",e);
        }

    }

    @Override
    public void executeSearch(RelatedProductSearch searchRequest) {
        disruptor.publishEvent(RelatedProductSearchTranslator.INSTANCE,searchRequest);
    }
}
