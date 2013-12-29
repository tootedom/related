package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;

/**
 * Represents a multi-map type lookup service that stores user search requests,
 * against the response context.  The response context allows us to obtain the
 * response based object through which we can send the results of the user search.
 *
 * The implementation must be thread safe.
 * There will NOT be multiple threads calling removeContexts
 * There will NOT be multiple threads calling addContext
 *
 * There will be a single thread calling removeContexts, and a single thread calling addContext.
 * These two threads will be running concurrently.  Therefore, the implementation must be thread
 * safe, as the remove and add methods will be called concurrently.
 */
public interface SearchResponseContextLookup<T> {

    /**
     * Removes the context holder.
     *
     * @param key the search key, which represents the user's search request
     * @return
     */
    public SearchResponseContextHolder[] removeContexts(SearchRequestLookupKey key);

    /**
     * Returns false if the search key already existed.
     * @param key
     * @param context the search context to add.
     * @return
     */
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContextHolder context);
}
