package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.api.indexing.RelatedProduct;

/**
 * Basically maps a product to a location (index name) it should be stored in.
 * The implementation must be thread safe.
 */

public interface RelatedProductStorageLocationMapper {
    /**
     * This must be thread safe.  Given a product, returns the location (i.e. index name)
     * that the product will be stored in.
     *
     * @param product
     * @return String representing the index the product is stored in
     */
    public String getLocationName(RelatedProduct product);
}
