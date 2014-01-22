package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import org.greencheek.related.api.indexing.RelatedItemReference;

/**
 * for use by the {@link RoundRobinRelatedItemIndexingMessageEventHandler} for taking {@link org.greencheek.related.api.indexing.RelatedItemReference}
 * objects and storing them in the.  The RelateProductReference is used a container for RelatedItem objects
 * that are generated from an {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
 */
public interface RelatedItemReferenceEventHandler extends EventHandler<RelatedItemReference> {
    public void shutdown();
}
