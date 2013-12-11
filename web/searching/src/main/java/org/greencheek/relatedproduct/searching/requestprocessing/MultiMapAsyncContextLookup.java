package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.AsyncContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores AsyncContext objects against a request url.
 * The AsyncContext can then be retrieved using the {@link SearchRequestLookupKey}
 * and the responses sent to the client that is waiting on the AsyncContext to be completed.
 */
public class MultiMapAsyncContextLookup implements AsyncContextLookup {

    private static final Logger log = LoggerFactory.getLogger(MultiMapAsyncContextLookup.class);
    private static final AsyncContext[] EMPTY_CONTEXT = new AsyncContext[0];

    private final Map<SearchRequestLookupKey,List<AsyncContext>> contexts;
    private final int expectedNumberOfSimilarRequests;

    public MultiMapAsyncContextLookup(Configuration config) {
        contexts = new HashMap<SearchRequestLookupKey, List<AsyncContext>>((int)Math.ceil(config.getSizeOfRelatedContentSearchRequestAndResponseQueue()/0.75));
        expectedNumberOfSimilarRequests = config.getNumberOfExpectedLikeForLikeRequests();
    }

    @Override
    public AsyncContext[] removeContexts(SearchRequestLookupKey key) {
        List<AsyncContext> ctxs = contexts.remove(key);
        if(ctxs==null) {
            return EMPTY_CONTEXT;
        }
        else {
           return ctxs.toArray(new AsyncContext[ctxs.size()]);
        }
    }

    @Override
    public void addContext(SearchRequestLookupKey key, AsyncContext context) {
        log.debug("adding context to key {}",key.toString());
        List<AsyncContext> ctxs = contexts.get(key);
        if(ctxs==null) {
            ctxs = new ArrayList<AsyncContext>(expectedNumberOfSimilarRequests);
            ctxs.add(context);
            contexts.put(key,ctxs);
        } else {
            ctxs.add(context);
        }

    }
}
