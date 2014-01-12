package org.greencheek.relatedproduct.searching.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

/**
 * Respresents either a Search Event, for which the response context requires storing for
 * later processing.  Or a Search Response event for which the response context requires retrieval for
 * for processing the search results.
 */
public class SearchEvent {
    private SearchEventType eventType;
    private SearchRequestLookupKey searchRequest;
    private SearchResponseContextHolder responseContextHolder;
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

    public void setSearchRequest(SearchRequestLookupKey searchRequest, SearchResponseContextHolder holder) {
        this.searchRequest = searchRequest;
        this.responseContextHolder = holder;
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

    public SearchResponseContextHolder getResponseContextHolder() {
        return responseContextHolder;
    }
}
