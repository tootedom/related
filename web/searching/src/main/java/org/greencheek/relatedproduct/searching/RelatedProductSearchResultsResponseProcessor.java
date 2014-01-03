package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;

/**
 * Is responsible for dealing with the array of search results, and sending those search results to
 * awaiting parties
 */
public interface RelatedProductSearchResultsResponseProcessor {
    public void handleResponse(SearchResultEventWithSearchRequestKey[] results);
    public void shutdown();
}
