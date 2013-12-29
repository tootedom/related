package org.greencheek.relatedproduct.searching.domain.api;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcomeType;
import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResults;

/**
 *
 */
public class SearchResultsEvent {

    public final static SearchResultsEvent EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent(RelatedProductSearchType.FREQUENTLY_RELATED_WITH, SearchResultsOutcomeType.EMPTY_RESULTS, FrequentlyRelatedSearchResults.EMPTY_RESULTS);
    public final static SearchResultsEvent EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent(RelatedProductSearchType.FREQUENTLY_RELATED_WITH, SearchResultsOutcomeType.FAILED_REQUEST, FrequentlyRelatedSearchResults.EMPTY_RESULTS);
    public final static SearchResultsEvent EMPTY_TIMED_OUT_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent(RelatedProductSearchType.FREQUENTLY_RELATED_WITH, SearchResultsOutcomeType.REQUEST_TIMEOUT, FrequentlyRelatedSearchResults.EMPTY_RESULTS);

    private final SearchResultsOutcomeType outcomeType;
    private final RelatedProductSearchType searchType;
    private final FrequentlyRelatedSearchResults frequentlyRelatedSearchResults;

    public SearchResultsEvent(RelatedProductSearchType searchType,
                              SearchResultsOutcomeType outcomeType,
                              Object results) {
        this.searchType = searchType;
        this.outcomeType = outcomeType;

        if(searchType == RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
            frequentlyRelatedSearchResults = (FrequentlyRelatedSearchResults)results;
        } else if (searchType == RelatedProductSearchType.MOST_RECENTLY_RELATED_WITH) {
            frequentlyRelatedSearchResults = null;
        } else {
            frequentlyRelatedSearchResults = null;
        }
    }

    public SearchResultsOutcomeType getOutcomeType() {
        return this.outcomeType;
    }

    public RelatedProductSearchType getSearchType() {
        return searchType;
    }

    public FrequentlyRelatedSearchResults getFrequentlyRelatedSearchResults() {
        return frequentlyRelatedSearchResults;
    }
}
