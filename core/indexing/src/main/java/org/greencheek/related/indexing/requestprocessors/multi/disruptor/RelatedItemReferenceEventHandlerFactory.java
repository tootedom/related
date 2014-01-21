package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

/**
 * Responsible for creating a {@linke RelatedItemReferenceEventHandler} that can be used
 * for dealing with {@link org.greencheek.related.api.indexing.RelatedItemReference} objects,
 * and storing them appropriately
 */
public interface RelatedItemReferenceEventHandlerFactory {

    /**
     * Either creates a new handler or reuses a common handler.
     * The handler internally deals with the storage/manipulation of
     * {@link org.greencheek.related.api.indexing.RelatedItemReference} objects
     *
     * @return The handler
     */
    RelatedItemReferenceEventHandler getHandler();
}
