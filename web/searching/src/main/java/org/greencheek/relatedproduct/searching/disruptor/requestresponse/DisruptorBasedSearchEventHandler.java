package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.domain.api.SearchRequestEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;

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
            event.setSearchRequestEvent(null);
            event.setSearchResultsEvent(null);
            event.setEventType(null);
            event.setRequestKey(null);
        }
    }

    @Override
    public void shutdown() {
        resultsResponseProcessor.shutdown();
    }

    private interface SearchEventHandler {
        public void handle(SearchEvent event);
    }

    private class SearchRequestHandler implements SearchEventHandler {
        @Override
        public void handle(SearchEvent event) {
            contextStorage.addContext(event.getRequestKey(), event.getSearchRequestEvent().getRequestContext());
        }
    }

    private class SearchResultHandler implements  SearchEventHandler {

        @Override
        public void handle(SearchEvent event) {
            resultsResponseProcessor.processSearchResults(contextStorage.removeContexts(event.getRequestKey()),event.getSearchResultsEvent());
        }
    }
}
