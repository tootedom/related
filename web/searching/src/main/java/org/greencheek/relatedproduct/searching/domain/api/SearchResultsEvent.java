package org.greencheek.relatedproduct.searching.domain.api;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultsEvent {

    public final static SearchResultsEvent EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResultsEvent(RelatedProductSearchType.FREQUENTLY_RELATED_WITH, FrequentlyRelatedSearchResults.EMPTY_RESULTS);

    private final RelatedProductSearchType searchType;
    private final FrequentlyRelatedSearchResults frequentlyRelatedSearchResults;

    public SearchResultsEvent(RelatedProductSearchType searchType, Object results) {
        this.searchType = searchType;

        if(searchType == RelatedProductSearchType.FREQUENTLY_RELATED_WITH) {
            frequentlyRelatedSearchResults = (FrequentlyRelatedSearchResults)results;
        } else if (searchType == RelatedProductSearchType.MOST_RECENTLY_RELATED_WITH) {
            frequentlyRelatedSearchResults = null;
        } else {
            frequentlyRelatedSearchResults = null;
        }
    }

    public RelatedProductSearchType getSearchType() {
        return searchType;
    }

    public FrequentlyRelatedSearchResults getFrequentlyRelatedSearchResults() {
        return frequentlyRelatedSearchResults;
    }
}
