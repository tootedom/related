package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.domain.api.SearchEvent;
import org.greencheek.relatedproduct.domain.api.SearchEventType;
import org.greencheek.relatedproduct.domain.api.SearchRequestEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

import javax.servlet.AsyncContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class SearchResponseEventTranslator implements EventTranslator<ResponseEvent> {


    private final List<AsyncContext> requestsToComplete;
    private final SearchResultsConverter results;

    public SearchResponseEventTranslator(List<AsyncContext> waitingRequests,
                                         SearchResultsConverter searchResults) {
        requestsToComplete = waitingRequests;
        results = searchResults;
    }

    @Override
    public void translateTo(ResponseEvent event, long sequence) {
        event.setContext(requestsToComplete);
        event.setResults(results);
    }
}
