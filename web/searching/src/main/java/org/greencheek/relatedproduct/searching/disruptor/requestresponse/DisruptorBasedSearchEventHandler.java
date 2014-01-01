package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.domain.api.SearchResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;

/**

 */
@Deprecated
public class DisruptorBasedSearchEventHandler implements SearchEventHandler {


    private final Configuration config;
    private final SearchResponseContextLookup contextStorage;
    private final RelatedProductSearchResultsResponseProcessor resultsResponseProcessor;
    private final SearchEventHandler[] eventHandlers = new SearchEventHandler[2];
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    public DisruptorBasedSearchEventHandler(Configuration config,
                                            SearchResponseContextLookup contextStorage,
                                            RelatedProductSearchResultsResponseProcessor resultsProcessor)
    {
        this.config = config;
        this.contextStorage = contextStorage;
        this.resultsResponseProcessor = resultsProcessor;
        eventHandlers[SearchEventType.SEARCH_REQUEST.getIndex()] = new SearchRequestHandler();
        eventHandlers[SearchEventType.SEARCH_RESULT.getIndex()] = new SearchResultHandler();
    }

    @Override
    public void onEvent(SearchResponseEvent event, long sequence, boolean endOfBatch) throws Exception {
//        try {
//            eventHandlers[event.getEventType().getIndex()].handle(event);
//        } finally {
//            event.setSearchResultsEventReference(null);
//            event.setEventType(null);
//            event.setRequestKeyReference(null);
//        }
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
        public void handle(SearchResponseEvent event);
    }

    private class SearchRequestHandler implements SearchEventHandler {
        @Override
        public void handle(SearchResponseEvent event) {
//            boolean executeSearch = contextStorage.addContext(event.getRequestKeyReference(), event.getSearchRequestEvent().getRequestContext());
//            if(executeSearch) {
//                RelatedProductSearchExecutor executor = event.getSearchRequestEvent().getSearchExecutor();
//                executor.executeSearch(event.getSearchRequestEvent().getSearchRequest());
//            }
        }
    }

    private class SearchResultHandler implements  SearchEventHandler {

        @Override
        public void handle(SearchResponseEvent event) {
//            resultsResponseProcessor.processSearchResults(contextStorage.removeContexts(event.getRequestKeyReference()),event.getSearchResultsEventReference());
        }
    }
}
