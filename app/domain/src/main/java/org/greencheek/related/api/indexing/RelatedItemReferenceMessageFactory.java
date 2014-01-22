package org.greencheek.related.api.indexing;

import com.lmax.disruptor.EventFactory;


/**
 * Simply creates a {@link RelatedItemReference}, which is basically a reference to a {@link RelatedItem} object.
 * The factory is used for populating the disruptor ring buffer.
 */
public class RelatedItemReferenceMessageFactory implements EventFactory<RelatedItemReference> {


    public RelatedItemReferenceMessageFactory() {

    }

    @Override
    public RelatedItemReference newInstance() {
        return new RelatedItemReference();
    }
}
