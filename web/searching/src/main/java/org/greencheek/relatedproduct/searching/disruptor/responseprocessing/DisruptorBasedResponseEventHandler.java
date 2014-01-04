package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandler;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Responsible for the processing of ResponseEvents from the ring buffer.
 * The response events represent search responses from the storage repository
 * for related products
 */
public class DisruptorBasedResponseEventHandler implements ResponseEventHandler {

    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedResponseEventHandler.class);


    private final SearchResultsConverterFactory converterLookup;
    private final SearchResponseContextHandlerLookup contextHandlerLookup;

    public DisruptorBasedResponseEventHandler(SearchResponseContextHandlerLookup responseContextHandler,
                                              SearchResultsConverterFactory factory)
    {
        this.converterLookup = factory;
        this.contextHandlerLookup = responseContextHandler;
    }

    @Override
    public void onEvent(ResponseEvent event, long sequence, boolean endOfBatch) throws Exception {
        handleResponseEvent(event);
    }

    @Override
    public void handleResponseEvent(ResponseEvent event) {
        try {
            SearchResultsEvent results = event.getResults();
            SearchResultsConverter converter = converterLookup.getConverter(results.getSearchType());

            if (converter == null) {
                if (log.isWarnEnabled()) {
                    log.warn("No factory available for converting search results of type : {}", results.getSearchType());
                }
                return;
            }

            SearchResponseContextHolder[] awaitingResponses = event.getContexts();

            if (awaitingResponses == null) {
                if (log.isWarnEnabled()) {
                    String res = converter.convertToString(results);
                    log.warn("No async responses waiting for search results : {}", res);
                }

                return;
            }

            log.debug("Sending search results to {} waiting responses", awaitingResponses.length);

            String response = converter.convertToString(results);
            String mediaType = converter.contentType();

            for(SearchResponseContextHolder contextHolder : awaitingResponses) {
                SearchResponseContext[] responseContexts = contextHolder.getContexts();
                log.debug("Sending search results to {} pending response listeners", responseContexts.length);

                for (SearchResponseContext sctx : responseContexts) {
                    SearchResponseContextHandler handler = contextHandlerLookup.getHandler(sctx.getContextType());
                    if(handler==null) {
                        log.error("No response handler defined for waiting response type: {}",sctx.getContextType());
                    } else {
                        try {
                            handler.sendResults(response, mediaType, results, sctx);
                        } finally {
                            sctx.close();
                        }
                    }

                }
            }
        } finally {
            event.setContexts(null);
            event.setResults(null);
        }
    }
}
