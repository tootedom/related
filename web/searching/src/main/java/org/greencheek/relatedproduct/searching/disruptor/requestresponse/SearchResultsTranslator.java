package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;

/**
 * Translates a {@link SearchResultEventWithSearchRequestKey} object into a {@link SearchEvent}
 */
public class SearchResultsTranslator implements EventTranslatorOneArg<SearchEvent,SearchResultEventWithSearchRequestKey> {

    public static final SearchResultsTranslator INSTANCE = new SearchResultsTranslator();

    public SearchResultsTranslator() {
    }

    @Override
    public void translateTo(SearchEvent event, long sequence, SearchResultEventWithSearchRequestKey searchResult) {
        event.setEventType(SearchEventType.SEARCH_RESULT);
        event.setSearchRequestEvent(null);
        event.setSearchResultsEvent(searchResult.getResponse());
        event.setRequestKey(searchResult.getRequest());
    }
}
