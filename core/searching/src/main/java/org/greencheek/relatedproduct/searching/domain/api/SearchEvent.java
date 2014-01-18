package org.greencheek.relatedproduct.searching.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

/**
 * Respresents either a Search Event, for which the response context requires storing for
 * later processing.  Or a Search Response event for which the response context requires retrieval for
 * for processing the search results.
 */
public class SearchEvent {
    private SearchEventType eventType;
    private SearchRequestLookupKey searchRequest;
    private SearchResponseContext[] responseContexts;
    private SearchResultEventWithSearchRequestKey[] searchResponse;

    public final static EventFactory<SearchEvent> FACTORY = new EventFactory<SearchEvent>()
    {
        @Override
        public SearchEvent newInstance()
        {
            return new SearchEvent();
        }
    };


    public void setEventType(SearchEventType eventType) {
        this.eventType = eventType;
    }

    public void setSearchRequest(SearchRequestLookupKey searchRequest, SearchResponseContext[] holder) {
        this.searchRequest = searchRequest;
        this.responseContexts = holder;
    }

    public void setSearchResponse(SearchResultEventWithSearchRequestKey[] searchResponse) {
        this.searchResponse = searchResponse;
    }

    public SearchEventType getEventType() {
        return eventType;
    }

    public SearchRequestLookupKey getSearchRequest() {
        return searchRequest;
    }

    public SearchResultEventWithSearchRequestKey[] getSearchResponse() {
        return searchResponse;
    }

    public SearchResponseContext[] getResponseContexts() {
        return responseContexts;
    }
}
