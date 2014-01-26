package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.searching.responseprocessing.SearchResponseContextHandler;
import org.greencheek.related.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Responsible for the processing of ResponseEvents from the ring buffer.
 * The response events represent search responses from the storage repository
 * for related products
 */
public class ResponseContextTypeBasedResponseEventHandler implements ResponseEventHandler {


    public static final String ERROR_RESPONSE = "{}";
    public static final String ERROR_MEDIA_TYPE = "application/json";

    private static final Logger log = LoggerFactory.getLogger(ResponseContextTypeBasedResponseEventHandler.class);


    private final SearchResultsConverterFactory converterLookup;
    private final SearchResponseContextHandlerLookup contextHandlerLookup;

    public ResponseContextTypeBasedResponseEventHandler(SearchResponseContextHandlerLookup responseContextHandler,
                                                        SearchResultsConverterFactory factory)
    {
        this.converterLookup = factory;
        this.contextHandlerLookup = responseContextHandler;
    }

    @Override
    public void handleResponseEvents(SearchResultEventWithSearchRequestKey[] searchResults,List<List<SearchResponseContext>> responseContexts) {
        for(int i=0;i<searchResults.length;i++) {
            log.debug("handling search result {}",i);
            handleResponseEvent(searchResults[i],responseContexts.get(i));
        }

    }

    /**
     * Nothing to shutdown
     */
    @Override
    public void shutdown() {

    }

    public void handleResponseEvent(SearchResultEventWithSearchRequestKey results,List<SearchResponseContext> awaitingResponses) {
        SearchResultsEvent event = results.getResponse();
            SearchResultsConverter converter = converterLookup.getConverter(event.getSearchResultsType());

            if (converter == null) {
                if (log.isWarnEnabled()) {
                    log.warn("No factory available for converting search results of type : {}", event.getSearchResultsType());
                }
            }

            if (awaitingResponses == null || awaitingResponses.size()==0) {
                if (log.isWarnEnabled() && converter!=null) {
                    String res = converter.convertToString(results);
                    log.warn("No async responses waiting for search results : {}", res);
                }

                return;
            }

            log.debug("Sending search results to {} waiting responses", awaitingResponses.size());

            String response = ERROR_RESPONSE;
            String mediaType = ERROR_MEDIA_TYPE;
            if(converter!=null) {
                response = converter.convertToString(results);
                mediaType = converter.contentType();
            } else {
                event = new SearchResultsEvent(SearchResultsOutcome.MISSING_SEARCH_RESULTS_HANDLER,event.getSearchResults());
            }

            for(SearchResponseContext sctx : awaitingResponses) {
                log.debug("Sending search results to {} pending response listener: {}", sctx.getContextType(), sctx);

                try {
                    SearchResponseContextHandler handler = contextHandlerLookup.getHandler(sctx.getContextType());

                    if (handler == null) {
                        log.error("No response handler defined for waiting response type: {}", sctx.getContextType());
                    } else {
                        handler.sendResults(response, mediaType, event, sctx);
                    }
                } finally {
                    sctx.close();
                }


            }

    }
}
