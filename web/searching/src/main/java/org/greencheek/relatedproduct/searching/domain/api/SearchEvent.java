package org.greencheek.relatedproduct.searching.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;


/**
 * This represents an event in the system that is either a request or a response.
 * The search event has a type to distinguish the two.
 *
 * As a result if the {@see #getEventType} is a {@link SearchEventType#SEARCH_REQUEST} then
 * the {@see #getSearchResultsEventReference} will be null.  Like wise if it is {@link SearchEventType#SEARCH_RESULT}
 * then {@see #getSearchRequestEvent} will be null.
 */
public class SearchEvent {

    private final SearchRequestEvent searchRequestEvent;

    private SearchEventType eventType;
    private SearchRequestLookupKey requestKeyReference;
    private SearchResultsEvent searchResultsEventReference;

    public SearchEvent() {
        searchRequestEvent = new SearchRequestEvent();
    }

    public SearchEventType getEventType() {
        return eventType;
    }

    public void setEventType(SearchEventType eventType) {
        this.eventType = eventType;
    }

    public SearchRequestEvent getSearchRequestEvent() {
        return searchRequestEvent;
    }


    public SearchRequestLookupKey getRequestKeyReference() {
        return requestKeyReference;
    }

    public void setRequestKeyReference(SearchRequestLookupKey requestKeyReference) {
        this.requestKeyReference = requestKeyReference;
    }

    public SearchResultsEvent getSearchResultsEventReference() {
        return this.searchResultsEventReference;
    }

    public void setSearchResultsEventReference(SearchResultsEvent event) {
        searchResultsEventReference = event;
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
