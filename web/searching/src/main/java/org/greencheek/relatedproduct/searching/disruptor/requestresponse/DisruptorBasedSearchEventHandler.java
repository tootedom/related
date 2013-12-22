package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedSearchEventHandler implements SearchEventHandler {


    private final Configuration config;
    private final AsyncContextLookup contextStorage;
    private final RelatedProductSearchResultsResponseProcessor resultsResponseProcessor;
    private final SearchEventHandler[] eventHandlers = new SearchEventHandler[2];
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    public DisruptorBasedSearchEventHandler(Configuration config,
                                            AsyncContextLookup contextStorage,
                                            RelatedProductSearchResultsResponseProcessor resultsProcessor)
    {
        this.config = config;
        this.contextStorage = contextStorage;
        this.resultsResponseProcessor = resultsProcessor;
        eventHandlers[SearchEventType.SEARCH_REQUEST.getIndex()] = new SearchRequestHandler();
        eventHandlers[SearchEventType.SEARCH_RESULT.getIndex()] = new SearchResultHandler();
    }

    @Override
    public void onEvent(SearchEvent event, long sequence, boolean endOfBatch) throws Exception {
        try {
            eventHandlers[event.getEventType().getIndex()].handle(event);
        } finally {
            event.setSearchResultsEventReference(null);
            event.setEventType(null);
            event.setRequestKeyReference(null);
        }
    }

    @Override
    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {
            try {
                resultsResponseProcessor.shutdown();
            } catch(Exception e) {

            }
        }
    }

    private interface SearchEventHandler {
        public void handle(SearchEvent event);
    }

    private class SearchRequestHandler implements SearchEventHandler {
        @Override
        public void handle(SearchEvent event) {
            boolean executeSearch = contextStorage.addContext(event.getRequestKeyReference(), event.getSearchRequestEvent().getRequestContext());
            if(executeSearch) {
                RelatedProductSearchExecutor executor = event.getSearchRequestEvent().getSearchExecutor();
                executor.executeSearch(event.getSearchRequestEvent().getSearchRequest());
            }
        }
    }

    private class SearchResultHandler implements  SearchEventHandler {

        @Override
        public void handle(SearchEvent event) {
            resultsResponseProcessor.processSearchResults(contextStorage.removeContexts(event.getRequestKeyReference()),event.getSearchResultsEventReference());
        }
    }
}
