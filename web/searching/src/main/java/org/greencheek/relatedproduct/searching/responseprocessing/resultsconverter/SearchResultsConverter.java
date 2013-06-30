package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public interface SearchResultsConverter {
    public String contentType();
    public String convertToString(SearchResultsEvent results);
}
