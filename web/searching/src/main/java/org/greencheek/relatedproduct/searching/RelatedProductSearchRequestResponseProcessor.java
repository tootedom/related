package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

import javax.servlet.AsyncContext;

/**
 * Interface that deals with take a user request and forwarding it on for processing
 * taking the results of many user requests and forwarding those results onto the appropriate
 * processor that sends that response to the waiting request.
 *
 */
public interface RelatedProductSearchRequestResponseProcessor {

    public void handleRequest(RelatedProductSearchRequest searchRequest);

    public void handleResponse(SearchResultEventWithSearchRequestKey[] results);

    public void shutdown();
}
