package org.greencheek.related.searching.requestprocessing;

/**
 * Holds references to an array of SearchResponseContext objects.
 * There could be multiple SearchResponseContext objects, as the result of a search
 * could be multiplexed (broadcast like a topic in an mq) to multiple places ie:
 *
 * - The user waiting for the search result
 * - Backend process logging search results
 *
 *
 * This class is not thread safe, and should only be used in a thread safe manner (i.e.
 * accessible within the confinds of the disruptor ring buffer)
 */
public class SearchResponseContextHolder {

    private static SearchResponseContext[] EMPTY_RESPONSES = new SearchResponseContext[0];
    private SearchResponseContext[] contexts;

    public SearchResponseContextHolder() {
    }


    public void setContexts(SearchResponseContext... contexts) {
        if(contexts==null) {
            contexts = EMPTY_RESPONSES;
        }
        this.contexts = contexts;
    }

    public SearchResponseContext[] getContexts() {
        return contexts;
    }
}
