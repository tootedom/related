package org.greencheek.related.searching;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;

/**
 * A go between for storing against a search request key a number of awaiting response objects.
 * These response object(s) are later retrieved, via the search request key, in order to send search
 * results to those awaiting response objects.  The response object naturally represents some means of
 * being able to send results to a location that is awaiting the search results.
 */
public interface RelatedItemSearchResultsToResponseGateway {

    /**
     * Associates with a certain search request, a ResponseContext.  The ResponseContext(s) can later be
     * retrieved in order to send search results to waiting parties
     */
    public void storeResponseContextForSearchRequest(SearchRequestLookupKey key, SearchResponseContext[] context);

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
