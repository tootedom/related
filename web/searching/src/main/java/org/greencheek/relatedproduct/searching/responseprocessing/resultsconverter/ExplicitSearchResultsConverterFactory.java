package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 29/06/2013
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class ExplicitSearchResultsConverterFactory implements SearchResultsConverterFactory {

    private final SearchResultsConverter frequentlyRelated;


    public ExplicitSearchResultsConverterFactory(SearchResultsConverter frequentlyRelated) {
        this.frequentlyRelated = frequentlyRelated;
    }


    @Override
    public SearchResultsConverter getConverter(RelatedProductSearchType searchType) {
        switch (searchType) {
            case FREQUENTLY_RELATED_WITH :
                return frequentlyRelated;
            case MOST_RECENTLY_RELATED_WITH:
                return frequentlyRelated;
            default:
                return frequentlyRelated;
        }
    }

}
