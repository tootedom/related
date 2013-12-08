package org.greencheek.relatedproduct.searching.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;


/**
 * This represents an event in the system that is either a request or a response.
 * The search event has a type to distinguish the two.
 *
 * As a result if the {@see #getEventType} is a {@link SearchEventType#SEARCH_REQUEST} then
 * the {@see #getSearchResultsEvent} will be null.  Like wise if it is {@link SearchEventType#SEARCH_RESULT}
 * then {@see #getSearchRequestEvent} will be null.
 */
public class SearchEvent {

    private SearchEventType eventType;
    private SearchRequestLookupKey requestKey;
    private SearchRequestEvent searchRequestEvent;
    private SearchResultsEvent searchResultsEvent;


    public SearchEventType getEventType() {
        return eventType;
    }

    public void setEventType(SearchEventType eventType) {
        this.eventType = eventType;
    }

    public SearchRequestLookupKey getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(SearchRequestLookupKey requestKey) {
        this.requestKey = requestKey;
    }

    public SearchRequestEvent getSearchRequestEvent() {
        return searchRequestEvent;
    }

    public void setSearchRequestEvent(SearchRequestEvent event) {
        this.searchRequestEvent = event;
    }

    public SearchResultsEvent getSearchResultsEvent() {
        return this.searchResultsEvent;
    }

    public void setSearchResultsEvent(SearchResultsEvent event) {
        searchResultsEvent = event;
    }


    public final static EventFactory<SearchEvent> FACTORY = new EventFactory<SearchEvent>()
    {
        @Override
        public SearchEvent newInstance()
        {
            return new SearchEvent();
        }
    };


}
