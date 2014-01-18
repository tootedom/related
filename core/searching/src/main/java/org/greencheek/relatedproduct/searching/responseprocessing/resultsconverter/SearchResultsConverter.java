package org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;

/**
 * Converts a SearchResultsEvent to a String representation.
 */
public interface SearchResultsConverter<T> {
    public String contentType();
    public String convertToString(SearchResultsEvent<T> results);
}
