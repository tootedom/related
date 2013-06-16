package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.domain.api.SearchEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.AsyncContext;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedResponseProcessor implements RelatedProductSearchResultsResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedResponseProcessor.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<ResponseEvent> disruptor;

    private final Configuration configuration;


    public DisruptorBasedResponseProcessor(EventHandler<ResponseEvent> eventHandler,
                                           Configuration configuration
    ) {
        this.configuration = configuration;
        disruptor = new Disruptor<ResponseEvent>(
                ResponseEvent.FACTORY,
                configuration.getSizeOfResponseProcessingQueue(), executorService,
                ProducerType.SINGLE, new SleepingWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    @PreDestroy
    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {
            try {
                log.info("Attempting to shut down executor thread pool in search request/response processor");
                executorService.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down executor thread pool in search request/response processor",e);
            }

            log.info("Shutting down index request processor");
            try {
                log.info("Attempting to shut down disruptor in search request/response processor");
                disruptor.halt();
                disruptor.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down disruptor in search request/response processor",e);
            }
        }
    }

    @Override
    public void processSearchResults(List<AsyncContext> context, SearchResultsConverter results) {
        disruptor.publishEvent(new SearchResponseEventTranslator(context,results));
    }
}
