package org.greencheek.related.searching.disruptor.responseprocessing;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.domain.api.*;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 *
 */
public class DisruptorRelatedItemSearchResultsToResponseGateway implements RelatedItemSearchResultsToResponseGateway {

    private static final Logger log = LoggerFactory.getLogger(DisruptorRelatedItemSearchResultsToResponseGateway.class);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final ExecutorService executorService;
    private final Disruptor<SearchEvent> disruptor;
    private final RingBuffer<SearchEvent> ringBuffer;


    private final EventTranslatorTwoArg<SearchEvent,SearchRequestLookupKey,SearchResponseContext[]> storeResponseContextTranslator =
            new EventTranslatorTwoArg<SearchEvent,SearchRequestLookupKey,SearchResponseContext[]>() {
                @Override
                public void translateTo(SearchEvent event, long sequence, SearchRequestLookupKey key, SearchResponseContext[] contextHolder) {
                    event.setEventType(SearchEventType.REQUEST);
                    event.setSearchRequest(key,contextHolder);
                }
            };

    private final EventTranslatorOneArg<SearchEvent,SearchResultEventWithSearchRequestKey[]> processSearchResultsTranslator =
            new EventTranslatorOneArg<SearchEvent,SearchResultEventWithSearchRequestKey[]>() {
                @Override
                public void translateTo(SearchEvent event, long sequence, SearchResultEventWithSearchRequestKey[] results) {
                    event.setEventType(SearchEventType.RESPONSE);
                    event.setSearchResponse(results);
                }
            };


    private final EventHandler<SearchEvent> eventHandler = new EventHandler<SearchEvent>() {
        @Override
        public void onEvent(SearchEvent event, long sequence, boolean endOfBatch) throws Exception {
            eventProcessors[event.getEventType().getIndex()].processSearchEvent(event);
        }
    };

    private final SearchEventProcessor[] eventProcessors = new SearchEventProcessor[2];

    public DisruptorRelatedItemSearchResultsToResponseGateway(Configuration configuration,
                                                              SearchEventProcessor requestProcessor,
                                                              SearchEventProcessor responseProcessor
    )

    {
        this.executorService = getExecutorService();
        disruptor = new Disruptor<SearchEvent>(
                SearchEvent.FACTORY,
                configuration.getSizeOfRelatedItemSearchRequestAndResponseQueue(), executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        ringBuffer = disruptor.start();
        eventProcessors[SearchEventType.REQUEST.getIndex()] = requestProcessor;
        eventProcessors[SearchEventType.RESPONSE.getIndex()] = responseProcessor;
    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("SearchResponseContextRepositoryGateway"));
    }


    @Override
    public void storeResponseContextForSearchRequest(SearchRequestLookupKey key, SearchResponseContext[] context) {
        ringBuffer.publishEvent(storeResponseContextTranslator,key,context);
    }

    @Override
    public void sendSearchResultsToResponseContexts(SearchResultEventWithSearchRequestKey[] multipleSearchResults) {
        ringBuffer.publishEvent(processSearchResultsTranslator,multipleSearchResults);
    }

    @Override
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
                disruptor.shutdown();
            } catch (Exception e) {
                log.warn("Unable to shut down disruptor in search request/response processor",e);
            }


            log.info("Shutting down search event processor that processing response handling");
            eventProcessors[SearchEventType.RESPONSE.getIndex()].shutdown();
        }
    }
}
