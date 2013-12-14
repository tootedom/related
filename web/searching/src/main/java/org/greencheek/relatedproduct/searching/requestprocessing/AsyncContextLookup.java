package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public interface AsyncContextLookup {
    public AsyncContext[] removeContexts(SearchRequestLookupKey key);

    /**
     * Returns false if the search key already existed.
     * @param key
     * @param context
     * @return
     */
    public boolean addContext(SearchRequestLookupKey key, AsyncContext context);
}
