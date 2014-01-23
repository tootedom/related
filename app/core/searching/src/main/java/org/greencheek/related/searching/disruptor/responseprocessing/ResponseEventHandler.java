package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

import java.util.List;

/**
 *
 */
public interface ResponseEventHandler {
    public void handleResponseEvents(SearchResultsEvent[] searchResults,List<List<SearchResponseContext>> responseContexts);
    public void shutdown();
}

