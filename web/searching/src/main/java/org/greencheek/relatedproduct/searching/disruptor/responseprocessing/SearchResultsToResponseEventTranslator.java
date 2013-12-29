package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;

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
