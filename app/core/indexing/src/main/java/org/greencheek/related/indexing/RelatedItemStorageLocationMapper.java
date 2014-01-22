package org.greencheek.related.indexing;

import org.greencheek.related.api.indexing.RelatedItem;

/**
 * Basically maps a product to a location (index name) it should be stored in.
 * The implementation must be thread safe.
 */

public interface RelatedItemStorageLocationMapper {
    /**
     * This must be thread safe.  Given a product, returns the location (i.e. index name)
     * that the product will be stored in.
     *
     * @param product
     * @return String representing the index the product is stored in
     */
    public String getLocationName(RelatedItem product);
}
