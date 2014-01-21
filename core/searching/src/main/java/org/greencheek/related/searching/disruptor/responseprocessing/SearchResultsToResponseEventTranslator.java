package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.searching.domain.api.ResponseEvent;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextHolder;

/**
 *
 */
public class SearchResultsToResponseEventTranslator implements SearchResponseEventTranslator {


    public static final SearchResultsToResponseEventTranslator INSTANCE = new SearchResultsToResponseEventTranslator();

    public SearchResultsToResponseEventTranslator() {

    }

    @Override
    public void translateTo(ResponseEvent event, long sequence, SearchResponseContextHolder[] waitingRequests,
                            SearchResultsEvent searchResults) {
        event.setContexts(waitingRequests);
        event.setResults(searchResults);
    }
}
