package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Given a search results class type, a SearchResultsConverter is returned that is able to
 * deal with converting that search response.
 */
public interface SearchResultsConverterFactory  {

    <T> SearchResultsConverter<T> getConverter(Class<T> searchType);
}
