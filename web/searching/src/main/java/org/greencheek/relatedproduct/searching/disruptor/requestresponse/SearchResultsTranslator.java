package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.relatedproduct.searching.domain.api.SearchResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;

/**
 * Translates a {@link SearchResultEventWithSearchRequestKey} object into a {@link org.greencheek.relatedproduct.searching.domain.api.SearchResponseEvent}
 */
public class SearchResultsTranslator implements EventTranslatorOneArg<SearchResponseEvent,SearchResultEventWithSearchRequestKey> {

    public static final SearchResultsTranslator INSTANCE = new SearchResultsTranslator();

    public SearchResultsTranslator() {
    }

    @Override
    public void translateTo(SearchResponseEvent event, long sequence, SearchResultEventWithSearchRequestKey searchResult) {
//        event.setEventType(SearchEventType.SEARCH_RESULT);
        event.setSearchResultsEventReference(searchResult.getResponse());
        event.setRequestKeyReference(searchResult.getRequest());
    }
}
