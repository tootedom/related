package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Stores SearchResponseContext objects against a request url.
 * The SearchResponseContext can then be retrieved using the {@link SearchRequestLookupKey}
 * and the responses sent to the client that is waiting on the SearchResponseContext.
 */
public class MultiMapSearchResponseContextLookup implements SearchResponseContextLookup {

    private static final Logger log = LoggerFactory.getLogger(MultiMapSearchResponseContextLookup.class);
    private static final List<SearchResponseContext> EMPTY_CONTEXT = Collections.EMPTY_LIST;

    private final Map<SearchRequestLookupKey,List<SearchResponseContext>> contexts;
    private final int expectedNumberOfSimilarRequests;

    public MultiMapSearchResponseContextLookup(Configuration config) {
        contexts = new HashMap<SearchRequestLookupKey,List<SearchResponseContext>>((int)Math.ceil(config.getSizeOfRelatedItemSearchRequestAndResponseQueue()/0.75));
        expectedNumberOfSimilarRequests = config.getNumberOfExpectedLikeForLikeRequests();
    }

    @Override
    public List<SearchResponseContext> removeContexts(SearchRequestLookupKey key) {
        List<SearchResponseContext> ctxs = contexts.remove(key);
        if(ctxs==null) {
            log.debug("No awaiting contexts for key: {}",key);
            return EMPTY_CONTEXT;
        }
        else {
            log.debug("{} awaiting contexts for key: {}",ctxs.size(),key);
            return ctxs;
        }
    }

    @Override
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContext[] contextObjs) {
        if(contextObjs == null) return false;
        log.debug("adding {} context()s",contextObjs.length);
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
