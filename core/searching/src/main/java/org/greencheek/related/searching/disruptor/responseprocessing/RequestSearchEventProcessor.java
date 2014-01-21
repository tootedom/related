package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.searching.domain.api.SearchEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContextLookup;

/**
 * Processes SearchEvents for which {@link org.greencheek.related.searching.domain.api.SearchEvent#getEventType()}
 * returns {@link org.greencheek.related.searching.domain.api.SearchEventType#REQUEST}
 *
 * This implementation takes the SearchEvent and obtains from it the {@link org.greencheek.related.api.searching.lookup.SearchRequestLookupKey}
 * and the associated response contexts {@link org.greencheek.related.searching.requestprocessing.SearchResponseContextHolder}
 * and stores the response context in the provided {@link org.greencheek.related.searching.requestprocessing.SearchResponseContextLookup}
 */
public class RequestSearchEventProcessor implements SearchEventProcessor {
    private final SearchResponseContextLookup searchContext;

    public RequestSearchEventProcessor(SearchResponseContextLookup searchContext) {
        this.searchContext = searchContext;
    }


    @Override
    public void processSearchEvent(SearchEvent event) {
        searchContext.addContext(event.getSearchRequest(), event.getResponseContexts());
    }

    @Override
    public void shutdown() {

    }
}
