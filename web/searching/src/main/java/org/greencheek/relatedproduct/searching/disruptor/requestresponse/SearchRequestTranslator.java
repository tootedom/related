package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.domain.api.SearchRequestEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequestTranslator implements EventTranslator<SearchEvent> {


    private final SearchRequestLookupKey requestKey;
    private final AsyncContext requestCtx;

    public SearchRequestTranslator(SearchRequestLookupKey key, AsyncContext request) {
        this.requestKey = key;
        this.requestCtx = request;
    }

    @Override
    public void translateTo(SearchEvent event, long sequence) {
        event.setSearchRequestEvent(new SearchRequestEvent(requestCtx));
        event.setEventType(SearchEventType.SEARCH_REQUEST);
        event.setRequestKey(requestKey);
    }
}
