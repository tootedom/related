package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

/**
 * Responsible for creating a {@linke RelatedProductReferenceEventHandler} that can be used
 * for dealing with {@link org.greencheek.relatedproduct.domain.RelatedProductReference} objects,
 * and storing them appropriately
 */
public interface RelatedProductReferenceEventHandlerFactory {

    /**
     * Either creates a new handler or reuses a common handler.
     * The handler internally deals with the storage/manipulation of
     * {@link org.greencheek.relatedproduct.domain.RelatedProductReference} objects
     *
     * @return The handler
     */
    RelatedProductReferenceEventHandler getHandler();
}
