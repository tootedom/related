package org.greencheek.relatedproduct.domain.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 25/06/2013
 * Time: 08:26
 * To change this template use File | Settings | File Templates.
 */
public class SearchResult {

    public final static SearchResult EMPTY_FREQUENTLY_RELATED_SEARCH_RESULTS = new SearchResult(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,FrequentlyRelatedSearchResults.EMPTY_RESULTS);

    private final RelatedProductSearchType searchType;
    private final FrequentlyRelatedSearchResults frequentlyRelatedSearchResults;


    public SearchResult(RelatedProductSearchType type, Object results) {
        this.searchType = type;
        this.frequentlyRelatedSearchResults = null;
    }

    public Object getResults() {
        return null;
    }

    public RelatedProductSearchType getSearchType() {
        return searchType;
    }
}
