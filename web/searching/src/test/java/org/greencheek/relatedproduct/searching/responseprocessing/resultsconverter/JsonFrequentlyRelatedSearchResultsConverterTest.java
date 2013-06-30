package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.domain.searching.FrequentlyRelatedSearchResults;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 23/06/2013
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class JsonFrequentlyRelatedSearchResultsConverterTest extends FrequentlyRelatedSearchResultsConverterTest{
    @Override
    public SearchResultsConverter getConverter() {
        return new JsonFrequentlyRelatedSearchResultsConverter(configuration);
    }

    @Override
    public String getExpectedContentType() {
        return "application/json";
    }
}
