package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;

/**
 * Represents a multi-map type lookup service that stores user search requests,
 * against the response context.  The response context allows us to obtain the
 * response based object through which we can send the results of the user search.
 *
 * There will only be one thread calling this data structure.  It will either be removing Contexts
 * or adding them.
 */
public interface SearchResponseContextLookup {

    /**
     * Removes the context holder.
     *
     * @param key the search key, which represents the user's search request
     * @return
     */
    public SearchResponseContext[] removeContexts(SearchRequestLookupKey key);

    /**
     * adds the given SearchResponseContextHolder against the given key.  If the key already exists,
     * then the context is added to that pre-existing list of contexts for that key (the key represents
     * a search request)
     *
     * @param key the search key representing  a user search request
     * @param context the search context to add.
     */
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] context);
}
