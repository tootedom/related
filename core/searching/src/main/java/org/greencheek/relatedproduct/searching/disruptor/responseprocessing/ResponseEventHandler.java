package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

/**
 *
 */
public interface ResponseEventHandler {
    public void handleResponseEvents(SearchResultsEvent[] searchResults,SearchResponseContext[][] responseContexts);
    public void shutdown();
}

