package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReference;

/**
 * for use by the {@link RoundRobinRelatedProductIndexingMessageEventHandler} for taking {@link RelatedProductReference}
 * objects and storing them in the.  The RelateProductReference is used a container for RelatedProduct objects
 * that are generated from an {@link org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage}
 */
public interface RelatedProductReferenceEventHandler extends EventHandler<RelatedProductReference> {
    public void shutdown();
}
