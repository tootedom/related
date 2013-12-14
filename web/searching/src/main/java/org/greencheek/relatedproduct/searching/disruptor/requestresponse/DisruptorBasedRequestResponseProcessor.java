package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Handles users search request and processes the responses for those requests.
 */
public class DisruptorBasedRequestResponseProcessor implements RelatedProductSearchRequestResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRequestResponseProcessor.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<SearchEvent> disruptor;

    private final Configuration configuration;


    public DisruptorBasedRequestResponseProcessor(SearchEventHandler eventHandler,
                                                  Configuration configuration
    ) {
        this.configuration = configuration;
        disruptor = new Disruptor<SearchEvent>(
                SearchEvent.FACTORY,
                configuration.getSizeOfRelatedContentSearchRequestAndResponseQueue(), executorService,
                ProducerType.MULTI, new SleepingWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    @Override
    public void handleRequest(RelatedProductSearchRequest searchRequest) {
//        RelatedProductSearch search = searchRequest.searchRequest;
        disruptor.publishEvent(SearchRequestTranslator.INSTANCE, searchRequest);
//                new SearchRequestTranslator(search.getLookupKey(),searchRequest.getRequestContext()));
    }

    @Override
    public void handleResponse(SearchResultEventWithSearchRequestKey[] results) {
        disruptor.publishEvents(SearchResultsTranslator.INSTANCE, results);
    }

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

}
