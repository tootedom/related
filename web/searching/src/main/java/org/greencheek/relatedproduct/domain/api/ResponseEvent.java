package org.greencheek.relatedproduct.domain.api;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

import javax.servlet.AsyncContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 07:11
 * To change this template use File | Settings | File Templates.
 */
public class ResponseEvent {
    private List<AsyncContext> context;
    private SearchResultsConverter results;


    public final static EventFactory<ResponseEvent> FACTORY = new EventFactory<ResponseEvent>()
    {
        @Override
        public ResponseEvent newInstance()
        {
            return new ResponseEvent();
        }
    };

    public List<AsyncContext> getContext() {
        return context;
    }

    public void setContext(List<AsyncContext> context) {
        this.context = context;
    }

    public SearchResultsConverter getResults() {
        return results;
    }

    public void setResults(SearchResultsConverter results) {
        this.results = results;
    }
}
