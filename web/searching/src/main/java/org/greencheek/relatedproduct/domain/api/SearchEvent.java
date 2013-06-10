package org.greencheek.relatedproduct.domain.api;

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
    private Object event;


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

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
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
