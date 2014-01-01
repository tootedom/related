package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.searching.RelatedProductSearchResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handles the processing of the responses for search requests.
 */
@Deprecated
public class DisruptorBasedRequestResponseProcessor implements RelatedProductSearchResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRequestResponseProcessor.class);

    private final SearchResponseContextLookup contextStorage;
    private final RelatedProductSearchResultsResponseProcessor responseProcessor;

    public DisruptorBasedRequestResponseProcessor(SearchResponseContextLookup contextStorage,
                                                  RelatedProductSearchResultsResponseProcessor responseProcessor) {
        this.contextStorage = contextStorage;
        this.responseProcessor = responseProcessor;
    }

    @Override
    public void handleResponse(SearchResultEventWithSearchRequestKey[] results) {
        for(SearchResultEventWithSearchRequestKey result : results) {
//            responseProcessor.processSearchResults(contextStorage.removeContexts(result.getRequest()),result.getResponse());
        }
    }

}
