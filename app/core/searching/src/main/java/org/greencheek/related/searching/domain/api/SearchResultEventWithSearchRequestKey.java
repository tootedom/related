package org.greencheek.related.searching.domain.api;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;

/**
 * A {@link SearchResultsEvent} associated with the {@link org.greencheek.related.api.searching.lookup.SearchRequestLookupKey}
 * that was responsible for that search result
 */
public class SearchResultEventWithSearchRequestKey<T> {
    private final SearchResultsEvent<T> result;
    private final SearchRequestLookupKey request;
    private final long searchExecutionTimeInMillis;
    private final long startOfSearchRequestProcessing;

    public SearchResultEventWithSearchRequestKey(SearchResultsEvent<T> result, SearchRequestLookupKey requestKey,
                                                 long searchExecutionTimeInMillis,long startOfSearchRequestProcessing) {
        this.request = requestKey;
        this.result = result;
        this.searchExecutionTimeInMillis = searchExecutionTimeInMillis;
        this.startOfSearchRequestProcessing = startOfSearchRequestProcessing;
    }


    public SearchRequestLookupKey getRequest() {
        return request;
    }

    public SearchResultsEvent<T> getResponse() {
        return result;
    }

    public long getSearchExecutionTime() {
        return searchExecutionTimeInMillis;
    }

    public long getStartOfSearchRequestProcessing() {
        return startOfSearchRequestProcessing;
    }
}
