package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;


import org.greencheek.relatedproduct.searching.domain.api.SearchEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Processes SearchEvents for which {@link org.greencheek.relatedproduct.searching.domain.api.SearchEvent#getEventType()}
 * returns {@link org.greencheek.relatedproduct.searching.domain.api.SearchEventType#RESPONSE}
 *
 * Given a SearchEvent obtains the associated array of {@link SearchResultEventWithSearchRequestKey} objects
 * obtaining the response contexts associated with search request ket for which the search was performed,
 * and passes these and the search result to the {@link org.greencheek.relatedproduct.searching.disruptor.responseprocessing.ResponseEventHandler}
 * for processing
 */
public class ResponseSearchEventProcessor implements SearchEventProcessor {
    private static final Logger log = LoggerFactory.getLogger(ResponseSearchEventProcessor.class);

    private final SearchResponseContextLookup searchContext;
    private final ResponseEventHandler responseEventHandler;

    public ResponseSearchEventProcessor(SearchResponseContextLookup searchContext,
                                        ResponseEventHandler responseHandler) {
        this.searchContext = searchContext;
        this.responseEventHandler = responseHandler;

    }

    @Override
    public void processSearchEvent(SearchEvent event) {
        SearchResultEventWithSearchRequestKey[] results = event.getSearchResponse();
        log.debug("Distributing {} search response(s) to awaiting parties",results.length);

        SearchResponseContext[][] responseContexts = new SearchResponseContext[results.length][];
        SearchResultsEvent[] searchResults = new SearchResultsEvent[results.length];

        for (int i = 0; i < results.length; i++) {
            SearchResultEventWithSearchRequestKey res = results[i];
            responseContexts[i] = searchContext.removeContexts(res.getRequest());
            searchResults[i] = res.getResponse();
        }

        log.debug("responseContexts {}", Arrays.toString(responseContexts[0]));
        responseEventHandler.handleResponseEvents(searchResults,responseContexts);
    }
}
