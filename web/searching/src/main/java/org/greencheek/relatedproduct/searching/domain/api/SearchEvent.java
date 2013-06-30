package org.greencheek.relatedproduct.searching.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:22
 * To change this template use File | Settings | File Templates.
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
