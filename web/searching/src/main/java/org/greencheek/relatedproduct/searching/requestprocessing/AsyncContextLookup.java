package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

import javax.servlet.AsyncContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public interface AsyncContextLookup {
    public List<AsyncContext> removeContexts(SearchRequestLookupKey key);
    public void addContext(SearchRequestLookupKey key, AsyncContext context);
}
