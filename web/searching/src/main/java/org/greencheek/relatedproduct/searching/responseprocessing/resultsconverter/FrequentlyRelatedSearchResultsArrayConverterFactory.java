package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;

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


//    public SearchResultsConverter getConverter(Object searchType) {
//        if(searchType instanceof RelatedProductSearchType) {
//            RelatedProductSearchType type = (RelatedProductSearchType)searchType;
//            switch (type) {
//                case FREQUENTLY_RELATED_WITH :
//                    return frequentlyRelated;
//                case MOST_RECENTLY_RELATED_WITH:
//                    return frequentlyRelated;
//                default:
//                    return frequentlyRelated;
//            }
//        } else {
//            return frequentlyRelated;
//        }
//
//
//    }

    @Override
    public <T> SearchResultsConverter<T> getConverter(Class<T> searchType) {
        if(searchType == FrequentlyRelatedSearchResult[].class) {
            return frequentlyRelated;
        } else {
            return null;
        }
    }
}
