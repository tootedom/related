package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.domain.api.SearchEvent;
import org.greencheek.relatedproduct.domain.api.SearchRequestEvent;
import org.greencheek.relatedproduct.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedResponseEventHandler implements EventHandler<ResponseEvent> {


    private final Configuration config;
    private final RelatedProductSearchResultsResponseProcessor resultsResponseProcessor;

    @Inject
    public DisruptorBasedResponseEventHandler(Configuration config,
                                              RelatedProductSearchResultsResponseProcessor resultsProcessor)
    {
        this.config = config;
        this.resultsResponseProcessor = resultsProcessor;

    }

    @Override
    public void onEvent(ResponseEvent event, long sequence, boolean endOfBatch) throws Exception {
        try {
            resultsResponseProcessor.processSearchResults(event.getContext(),event.getResults());
        } finally {
            event.setContext(null);
            event.setResults(null);
        }

    }
}
