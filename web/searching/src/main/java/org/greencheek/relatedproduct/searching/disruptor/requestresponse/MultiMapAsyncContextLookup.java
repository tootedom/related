package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.AsyncContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
@Named
public class MultiMapAsyncContextLookup implements AsyncContextLookup{

    private final Map<SearchRequestLookupKey,List<AsyncContext>> contexts;
    private final int expectedNumberOfSimilarRequests;

    @Inject
    public MultiMapAsyncContextLookup(Configuration config) {
        contexts = new HashMap<SearchRequestLookupKey,List<AsyncContext>>(config.getSizeOfRelatedContentSearchRequestAndResponseQueue());
        expectedNumberOfSimilarRequests = config.getNumberOfExpectedLikeForLikeRequests();
    }

    @Override
    public List<AsyncContext> removeContexts(SearchRequestLookupKey key) {
        return contexts.remove(key);
    }

    @Override
    public boolean addContext(SearchRequestLookupKey key, AsyncContext context) {
        List<AsyncContext> ctxs = contexts.get(key);
        if(ctxs==null) {
            ctxs = new ArrayList<AsyncContext>(expectedNumberOfSimilarRequests);
            ctxs.add(context);
            contexts.put(key,ctxs);
            return false;
        } else {
            ctxs.add(context);
            return true;
        }

    }
}
