package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
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


    private final AsyncContext[] requestsToComplete;
    private final SearchResultsEvent results;

    public SearchResponseEventTranslator(AsyncContext[] waitingRequests,
                                         SearchResultsEvent searchResults) {
        requestsToComplete = waitingRequests;
        results = searchResults;
    }

    @Override
    public void translateTo(ResponseEvent event, long sequence) {
        event.setContext(requestsToComplete);
        event.setResults(results);
    }
}
