package org.greencheek.related.searching.repository;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;

/**
 * Used to process MultiSearchResponse's from the elasticsearch that related to RelatedItemSearch,
 * converting the multi search response objects to SearchResultEventWithSearchRequestKey
 */
public interface MultiSearchResponseProcessor<T> {
    public SearchResultEventWithSearchRequestKey<T>[] processMultiSearchResponse(RelatedItemSearch[] searches,MultiSearchResponse searchResponse);
}
