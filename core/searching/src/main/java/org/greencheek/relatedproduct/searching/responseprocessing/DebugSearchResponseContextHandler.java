package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Outputs the given results to the logger
 */
public class DebugSearchResponseContextHandler implements SearchResponseContextHandler {
    private static final Logger log = LoggerFactory.getLogger(DebugSearchResponseContextHandler.class);
    public static final DebugSearchResponseContextHandler INSTANCE = new DebugSearchResponseContextHandler();

    @Override
    public void sendResults(String resultsAsString, String mediaType, SearchResultsEvent results, SearchResponseContext sctx) {
        log.debug("Content-Type:{}[{}]",mediaType,resultsAsString);
        sctx.close();
    }
}
