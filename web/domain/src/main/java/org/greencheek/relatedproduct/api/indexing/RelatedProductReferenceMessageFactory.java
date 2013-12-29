package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;


/**
 * Simply creates a {@link RelatedProductReference}, which is basically a reference to a {@link RelatedProduct} object.
 * The factory is used for populating the disruptor ring buffer.
 */
public class RelatedProductReferenceMessageFactory implements EventFactory<RelatedProductReference> {


    public RelatedProductReferenceMessageFactory() {

    }

    @Override
    public RelatedProductReference newInstance() {
        return new RelatedProductReference();
    }
}
