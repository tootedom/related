package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:01
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultsTranslator implements EventTranslator<SearchEvent> {

    private final SearchRequestLookupKey requestKey;
    private final SearchResultsConverter results;

    public SearchResultsTranslator(SearchRequestLookupKey key, SearchResultsConverter results) {
        this.requestKey = key;
        this.results = results;
    }

    @Override
    public void translateTo(SearchEvent event, long sequence) {
        event.setEventType(SearchEventType.SEARCH_RESULT);
        event.setEvent(new SearchResultsEvent(results));
        event.setRequestKey(requestKey);
    }
}
