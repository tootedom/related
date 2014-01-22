package org.greencheek.related.searching;

import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.util.config.Configuration;

/**
 * performs a number of searches, either in batch fashion or one at a time.
 * The list of result are returned associated with the given search request
 * which cna be forward to awaiting parties
 */
public interface RelatedItemSearchRepository<T> {
    public SearchResultEventWithSearchRequestKey<T>[] findRelatedItems(Configuration config, RelatedItemSearch[] searches);
    public void shutdown();
}
