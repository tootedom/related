package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * performs a number of searches, either in batch fasion to others,
 * and calls {@link RelatedProductSearchResponseProcessor#handleResponse(org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey, org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent)}
 * to forward the results of the search to the listener.
 */
public interface RelatedProductSearchRepository {
    public SearchResultEventWithSearchRequestKey[] findRelatedProducts(Configuration config, RelatedProductSearch[] searches);
    public void shutdown();
}
