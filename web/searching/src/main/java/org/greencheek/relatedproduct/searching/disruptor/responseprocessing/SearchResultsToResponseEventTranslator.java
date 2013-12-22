package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;

import javax.servlet.AsyncContext;

/**
 *
 */
public class SearchResultsToResponseEventTranslator implements SearchResponseEventTranslator {


    public static final SearchResultsToResponseEventTranslator INSTANCE = new SearchResultsToResponseEventTranslator();

    public SearchResultsToResponseEventTranslator() {

    }

    @Override
    public void translateTo(ResponseEvent event, long sequence,AsyncContext[] waitingRequests,
                            SearchResultsEvent searchResults) {
        event.setContext(waitingRequests);
        event.setResults(searchResults);
    }
}
