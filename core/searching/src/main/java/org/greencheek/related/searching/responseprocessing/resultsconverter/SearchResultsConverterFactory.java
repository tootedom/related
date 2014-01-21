package org.greencheek.related.searching.responseprocessing.resultsconverter;

/**
 * Given a search results class type, a SearchResultsConverter is returned that is able to
 * deal with converting that search response.
 */
public interface SearchResultsConverterFactory  {

    <T> SearchResultsConverter<T> getConverter(Class<T> searchType);
}
