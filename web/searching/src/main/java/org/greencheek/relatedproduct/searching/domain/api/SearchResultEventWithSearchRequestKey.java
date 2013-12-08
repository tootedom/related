package org.greencheek.relatedproduct.searching.domain.api;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

/**
 * A {@link SearchResultsEvent} associated with the {@link org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey}
 * that was responsible for that search result
 */
public class SearchResultEventWithSearchRequestKey {
    private final SearchResultsEvent result;
    private final SearchRequestLookupKey request;

    public SearchResultEventWithSearchRequestKey(SearchResultsEvent result, SearchRequestLookupKey requestKey) {
        this.request = requestKey;
        this.result = result;
    }


    public SearchRequestLookupKey getRequest() {
        return request;
    }

    public SearchResultsEvent getResponse() {
        return result;
    }

}
