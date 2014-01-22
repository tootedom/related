package org.greencheek.related.searching.responseprocessing.resultsconverter;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 29/06/2013
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedSearchResultsArrayConverterFactory implements SearchResultsConverterFactory {

    private final SearchResultsConverter frequentlyRelated;


    public FrequentlyRelatedSearchResultsArrayConverterFactory(SearchResultsConverter<FrequentlyRelatedSearchResult[]> frequentlyRelated) {
        this.frequentlyRelated = frequentlyRelated;
    }

    @Override
    public <T> SearchResultsConverter<T> getConverter(Class<T> searchType) {
        if(searchType == FrequentlyRelatedSearchResult[].class) {
            return frequentlyRelated;
        } else {
            return null;
        }
    }
}
