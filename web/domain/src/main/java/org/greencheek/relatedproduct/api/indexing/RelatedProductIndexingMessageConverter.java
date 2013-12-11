package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.domain.RelatedProduct;

import java.util.Set;

/**
 * Converts an {@link RelatedProductIndexingMessage} into an array of
 * {@link RelatedProduct} objects.  Each returned RelatedProduct
 * has a link to the ids of the other RelatedProduct objects that are represented
 * in the {@link RelatedProductIndexingMessage} the method
 * {@link org.greencheek.relatedproduct.domain.RelatedProduct#getRelatedProductPids()} returns the
 * related product ids
 */
public interface RelatedProductIndexingMessageConverter {

    /**
     * There is no guarantee of the ordering of the RelatedProduct array is in the
     * same order as {@link RelatedProductSet#relatedProducts}
     *
     * @param message
     * @return an array of RelatedProducts
     */
    public RelatedProduct[] convertFrom(RelatedProductIndexingMessage message);
}
