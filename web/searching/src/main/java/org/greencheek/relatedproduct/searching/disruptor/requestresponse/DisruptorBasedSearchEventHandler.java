package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchRequestEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedSearchEventHandler implements SearchEventHandler {


    private final Configuration config;
    private final AsyncContextLookup contextStorage;
    private final RelatedProductSearchResultsResponseProcessor resultsResponseProcessor;

    public DisruptorBasedSearchEventHandler(Configuration config,
                                            AsyncContextLookup contextStorage,
                                            RelatedProductSearchResultsResponseProcessor resultsProcessor)
    {
        this.config = config;
        this.contextStorage = contextStorage;
        this.resultsResponseProcessor = resultsProcessor;

    }

    @Override
    public void onEvent(SearchEvent event, long sequence, boolean endOfBatch) throws Exception {
        try {
            switch(event.getEventType()) {
                case SEARCH_REQUEST :
                    contextStorage.addContext(event.getRequestKey(),((SearchRequestEvent)event.getEvent()).getRequestContext());
                    break;
                case SEARCH_RESULT:   // would be best to wrap in own stuff
                    resultsResponseProcessor.processSearchResults(contextStorage.removeContexts(event.getRequestKey()), ((SearchResultsEvent) event.getEvent()).getResults());

            }
        } finally {
            event.setEvent(null);
            event.setEventType(null);
            event.setRequestKey(null);
        }
    }

    @Override
    public void shutdown() {
        resultsResponseProcessor.shutdown();
    }
}
