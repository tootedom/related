package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.searching.domain.api.SearchEvent;

public interface SearchEventProcessor {
    public void processSearchEvent(SearchEvent event);
    public void shutdown();
}