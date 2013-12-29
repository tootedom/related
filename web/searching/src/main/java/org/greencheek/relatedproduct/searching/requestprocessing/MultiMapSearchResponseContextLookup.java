package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Stores SearchResponseContext objects against a request url.
 * The SearchResponseContext can then be retrieved using the {@link SearchRequestLookupKey}
 * and the responses sent to the client that is waiting on the SearchResponseContext.
 */
public class MultiMapSearchResponseContextLookup implements SearchResponseContextLookup {

    private static final Logger log = LoggerFactory.getLogger(MultiMapSearchResponseContextLookup.class);
    private static final SearchResponseContextHolder[] EMPTY_CONTEXT = new SearchResponseContextHolder[0];

    private final ConcurrentMap<SearchRequestLookupKey,SearchResponseContextHolder[]> contexts;

    public MultiMapSearchResponseContextLookup(Configuration config) {
        contexts = new ConcurrentHashMap<>((int)Math.ceil(config.getSizeOfRelatedContentSearchRequestAndResponseQueue()/0.75));
    }

    @Override
    public SearchResponseContextHolder[] removeContexts(SearchRequestLookupKey key) {
        SearchResponseContextHolder[] ctxs = contexts.remove(key);
        if(ctxs==null) {
            return EMPTY_CONTEXT;
        }
        else {
           return ctxs;
        }
    }

    @Override
    public boolean addContext(SearchRequestLookupKey key, SearchResponseContextHolder context) {

        SearchResponseContextHolder[] ctxs = contexts.get(key);
        if(ctxs==null) {
            contexts.put(key, new SearchResponseContextHolder[] { context });
            log.debug("added context to new key {}",key.toString());
            return true;
        } else {
            int length = ctxs.length;
            SearchResponseContextHolder[] newCtxs = new SearchResponseContextHolder[length+1];
            System.arraycopy(ctxs,0,newCtxs,0,length);
            newCtxs[length] = context;
            SearchResponseContextHolder[] oldReplaced = contexts.replace(key,newCtxs);
            if(oldReplaced==null) {
                contexts.put(key,new SearchResponseContextHolder[] { context });
            }
            log.debug("added context to existing key {}",key.toString());
            return false;
        }

    }
}
