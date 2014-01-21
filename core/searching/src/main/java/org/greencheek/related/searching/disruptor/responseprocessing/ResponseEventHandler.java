package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

/**
 *
 */
public interface ResponseEventHandler {
    public void handleResponseEvents(SearchResultsEvent[] searchResults,SearchResponseContext[][] responseContexts);
    public void shutdown();
}

