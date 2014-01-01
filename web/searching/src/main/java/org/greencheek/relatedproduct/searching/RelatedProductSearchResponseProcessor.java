package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;

/**
 * Interface that deals with taking a user request and forwarding it on for processing and also
 * for taking the results of many user requests and forwarding those results onto the appropriate
 * processor that sends that response to the waiting request.
 *
 */
public interface RelatedProductSearchResponseProcessor {

    public void handleResponse(SearchResultEventWithSearchRequestKey[] results);
}
