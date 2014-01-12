package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Stores SearchResponseContext objects against a request url.
 * The SearchResponseContext can then be retrieved using the {@link SearchRequestLookupKey}
 * and the responses sent to the client that is waiting on the SearchResponseContext.
 */
public class MultiMapSearchResponseContextLookup implements SearchResponseContextLookup {

    private static final Logger log = LoggerFactory.getLogger(MultiMapSearchResponseContextLookup.class);
    private static final SearchResponseContextHolder[] EMPTY_CONTEXT = new SearchResponseContextHolder[0];

    private final Map<SearchRequestLookupKey,List<SearchResponseContextHolder>> contexts;
    private final int expectedNumberOfSimilarRequests;

    public MultiMapSearchResponseContextLookup(Configuration config) {
        contexts = new HashMap<SearchRequestLookupKey,List<SearchResponseContextHolder>>((int)Math.ceil(config.getSizeOfRelatedContentSearchRequestAndResponseQueue()/0.75));
        expectedNumberOfSimilarRequests = config.getNumberOfExpectedLikeForLikeRequests();
    }

    @Override
    public SearchResponseContextHolder[] removeContexts(SearchRequestLookupKey key) {
        List<SearchResponseContextHolder> ctxs = contexts.remove(key);
        if(ctxs==null) {
            return EMPTY_CONTEXT;
        }
        else {
            return ctxs.toArray(new SearchResponseContextHolder[ctxs.size()]);
        }
    }

    @Override
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContextHolder context) {

        List<SearchResponseContextHolder> ctxs = contexts.get(key);
        if(ctxs==null) {
            ctxs = new ArrayList<SearchResponseContextHolder>(expectedNumberOfSimilarRequests);
            ctxs.add(context);
            contexts.put(key,ctxs);
            log.debug("added context to new key {}",key.toString());
            return true;
        } else {
            ctxs.add(context);
            log.debug("added context to existing key {}",key.toString());
            return false;
        }

    }
}
