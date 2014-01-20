package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandler;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.relatedproduct.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


/**
 * Uses a ringbuffer as means of batching, and asychronously sending results to awaiting
 * clients.
 */
public class DisruptorBasedResponseContextTypeBasedResponseEventHandler implements ResponseEventHandler {


    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedResponseContextTypeBasedResponseEventHandler.class);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService;
    private final Disruptor<SearchResultsToDistributeToResponseContexts> disruptor;
    private final RingBuffer<SearchResultsToDistributeToResponseContexts> ringBuffer;

    private final ResponseEventHandler delegateHandler;

    private final EventHandler<SearchResultsToDistributeToResponseContexts> EVENT_HANDLER = new EventHandler<SearchResultsToDistributeToResponseContexts>() {
        @Override
        public void onEvent(SearchResultsToDistributeToResponseContexts event, long sequence, boolean endOfBatch) throws Exception {
            delegateHandler.handleResponseEvents(event.getSearchResultsEvents(),event.getResponseContexts());
        }
    };

    EventTranslatorTwoArg<SearchResultsToDistributeToResponseContexts,SearchResultsEvent[],SearchResponseContext[][]> translator = new EventTranslatorTwoArg<SearchResultsToDistributeToResponseContexts,SearchResultsEvent[],SearchResponseContext[][]>() {
        @Override
        public void translateTo(SearchResultsToDistributeToResponseContexts event, long sequence, SearchResultsEvent[] arg0, SearchResponseContext[][] arg1) {
            event.setSearchResultsEvents(arg0);
            event.setResponseContexts(arg1);
        }
    };

    private final static EventFactory<SearchResultsToDistributeToResponseContexts> FACTORY = new EventFactory<SearchResultsToDistributeToResponseContexts>()
    {
        @Override
        public SearchResultsToDistributeToResponseContexts newInstance()
        {
            return new SearchResultsToDistributeToResponseContexts();
        }
    };

    public DisruptorBasedResponseContextTypeBasedResponseEventHandler(Configuration configuration,
                                                                      ResponseEventHandler delegate)
    {
        this.delegateHandler = delegate;
        this.executorService = getExecutorService();
        disruptor = new Disruptor<SearchResultsToDistributeToResponseContexts>(
                FACTORY,
                configuration.getSizeOfResponseProcessingQueue(), executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {EVENT_HANDLER});

        ringBuffer = disruptor.start();
    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("SearchResponseProcessingQueue"));
    }


    @Override
    public void handleResponseEvents(SearchResultsEvent[] searchResults,SearchResponseContext[][] responseContexts) {
        ringBuffer.publishEvent(translator,searchResults,responseContexts);
    }

    @Override
    public void shutdown() {
       if(shutdown.compareAndSet(false,true)) {
           try {
               log.debug("Shutting down SearchResponseProcessingQueue ring buffer");
               disruptor.shutdown();
           } catch(Exception e) {

           }

           try {
               log.debug("Shutting down SearchResponseProcessingQueue thread pool");
               executorService.shutdownNow();
           } catch (Exception e) {

           }
       }
    }



    private static class SearchResultsToDistributeToResponseContexts {
        private SearchResultsEvent[] searchResultsEvents;
        private SearchResponseContext[][] responseContexts;


        public SearchResultsEvent[] getSearchResultsEvents() {
            return searchResultsEvents;
        }

        public void setSearchResultsEvents(SearchResultsEvent[] searchResultsEvents) {
            this.searchResultsEvents = searchResultsEvents;
        }

        public SearchResponseContext[][] getResponseContexts() {
            return responseContexts;
        }

        public void setResponseContexts(SearchResponseContext[][] responseContexts) {
            this.responseContexts = responseContexts;
        }
    }
}
