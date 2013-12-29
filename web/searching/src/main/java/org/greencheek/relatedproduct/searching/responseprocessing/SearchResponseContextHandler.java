package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;

/**
 *  Responsible for either outputting the given string content to the
 *  Context held within the SearchResponseContext, or converts the SearchResultsEvent so that it can be output
 *  to the context held in the SearchResponseContext
 */
public interface SearchResponseContextHandler<T> {
    public void sendResults(String resultsAsString,String mediaType,SearchResultsEvent results,SearchResponseContext<T> sctx);
}
