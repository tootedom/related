package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;

public interface SearchEventProcessor {
    public void processSearchEvent(SearchEvent event);
}