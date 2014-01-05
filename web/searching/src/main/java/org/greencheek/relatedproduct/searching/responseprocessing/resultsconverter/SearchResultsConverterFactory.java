package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 25/06/2013
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public interface SearchResultsConverterFactory  {

    <T> SearchResultsConverter<T> getConverter(Class<T> searchType);
}
