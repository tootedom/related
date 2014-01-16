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
    private static final SearchResponseContext[] EMPTY_CONTEXT = new SearchResponseContext[0];

    private final Map<SearchRequestLookupKey,List<SearchResponseContext>> contexts;
    private final int expectedNumberOfSimilarRequests;

    public MultiMapSearchResponseContextLookup(Configuration config) {
        contexts = new HashMap<SearchRequestLookupKey,List<SearchResponseContext>>((int)Math.ceil(config.getSizeOfRelatedContentSearchRequestAndResponseQueue()/0.75));
        expectedNumberOfSimilarRequests = config.getNumberOfExpectedLikeForLikeRequests();
    }

    @Override
    public SearchResponseContext[] removeContexts(SearchRequestLookupKey key) {
        List<SearchResponseContext> ctxs = contexts.remove(key);
        if(ctxs==null) {
            log.debug("No awaiting contexts for key: {}",key);
            return EMPTY_CONTEXT;
        }
        else {
            log.debug("{} awaiting contexts for key: {}",ctxs.size(),key);
            return ctxs.toArray(new SearchResponseContext[ctxs.size()]);
        }
    }

    @Override
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] contextObjs) {
        if(contextObjs == null) return false;
        log.debug("adding context {}",contexts);
        List<SearchResponseContext> ctxs = contexts.get(key);
        if(ctxs==null) {
            ctxs = new ArrayList<SearchResponseContext>(expectedNumberOfSimilarRequests);
            for(SearchResponseContext ctx : contextObjs) {
                ctxs.add(ctx);
            }
            contexts.put(key,ctxs);
            log.debug("added context to new key {}",key.toString());
            return true;
        } else {
            for(SearchResponseContext ctx : contextObjs) {
                ctxs.add(ctx);
            }
            log.debug("added context to existing key {}",key.toString());
            return false;
        }

    }
}
