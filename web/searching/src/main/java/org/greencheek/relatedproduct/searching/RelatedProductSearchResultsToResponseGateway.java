package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

/**
 * A go between for storing against a search request key a number of awaiting response objects.
 * These response object(s) are later retrieved, via the search request key, in order to send search
 * results to those awaiting response objects.  The response object naturally represents some means of
 * being able to send results to a location that is awaiting the search results.
 */
public interface RelatedProductSearchResultsToResponseGateway {

    /**
     * Associates with a certain search request, a ResponseContext.  The ResponseContext(s) can later be
     * retrieved in order to send search results to waiting parties
     */
    public void storeResponseContextForSearchRequest(SearchRequestLookupKey key, SearchResponseContextHolder context);

    /**
     * A search has completed and needs to be sent to the stored response processors
     * @param multipleSearchResults
     */
    public void sendSearchResultsToResponseContexts(SearchResultEventWithSearchRequestKey[] multipleSearchResults);

    /**
     * shutdown the response gateway
     */
    public void shutdown();
}
