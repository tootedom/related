package org.greencheek.related.searching.domain.api;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.SearchResultsOutcome;

/**
 *
 */
public class SearchResultsEvent<T> {

    private final static FrequentlyRelatedSearchResult[] EMPTY_FRSR= new FrequentlyRelatedSearchResult[0];

    public final static SearchResultsEvent<FrequentlyRelatedSearchResult[]> EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent<FrequentlyRelatedSearchResult[]>(SearchResultsOutcome.EMPTY_RESULTS, EMPTY_FRSR);
    public final static SearchResultsEvent<FrequentlyRelatedSearchResult[]> EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent<FrequentlyRelatedSearchResult[]>(SearchResultsOutcome.FAILED_REQUEST,EMPTY_FRSR);
    public final static SearchResultsEvent<FrequentlyRelatedSearchResult[]> EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent<FrequentlyRelatedSearchResult[]>(SearchResultsOutcome.REQUEST_TIMEOUT, EMPTY_FRSR);

    private final SearchResultsOutcome outcomeType;
    private final Class searchResultsType;
    private final T searchResults;

    public SearchResultsEvent(SearchResultsOutcome outcomeType,
                              T results) {
        this.outcomeType = outcomeType;
        this.searchResults = results;
        this.searchResultsType = results.getClass();

    }

    public SearchResultsOutcome getOutcomeType() {
        return this.outcomeType;
    }

    public Class<T> getSearchResultsType() {
        return searchResultsType;
    }

    public T getSearchResults() {
        return searchResults;
    }
}
