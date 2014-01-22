package org.greencheek.related.searching.domain.api;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;

/**
 * A {@link SearchResultsEvent} associated with the {@link org.greencheek.related.api.searching.lookup.SearchRequestLookupKey}
 * that was responsible for that search result
 */
public class SearchResultEventWithSearchRequestKey<T> {
    private final SearchResultsEvent<T> result;
    private final SearchRequestLookupKey request;

    public SearchResultEventWithSearchRequestKey(SearchResultsEvent<T> result, SearchRequestLookupKey requestKey) {
        this.request = requestKey;
        this.result = result;
    }


    public SearchRequestLookupKey getRequest() {
        return request;
    }

    public SearchResultsEvent<T> getResponse() {
        return result;
    }

}
