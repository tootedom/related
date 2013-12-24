package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

import javax.servlet.AsyncContext;

/**
 * Represents a multi-map type lookup service that stores user search requests,
 * against the response context.  The response context allows us to obtain the
 * response based object through which we can send the results of the user search.
 *
 *
 */
public interface SearchResponseContextLookup {
    public AsyncContext[] removeContexts(SearchRequestLookupKey key);

    /**
     * Returns false if the search key already existed.
     * @param key
     * @param context
     * @return
     */
    public boolean addContext(SearchRequestLookupKey key, AsyncContext context);
}
