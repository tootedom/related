package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;

/**
 * Used to process MultiSearchResponse's from the elasticsearch that related to RelatedProductSearch,
 * converting the multi search response objects to SearchResultEventWithSearchRequestKey
 */
public interface MultiSearchResponseProcessor<T> {
    public SearchResultEventWithSearchRequestKey<T>[] processMultiSearchResponse(RelatedProductSearch[] searches,MultiSearchResponse searchResponse);
}
