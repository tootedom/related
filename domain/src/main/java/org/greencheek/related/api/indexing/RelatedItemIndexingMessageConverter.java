package org.greencheek.related.api.indexing;

/**
 * Converts an {@link RelatedItemIndexingMessage} into an array of
 * {@link RelatedItem} objects.  Each returned RelatedItem
 * has a link to the ids of the other RelatedItem objects that are represented
 * in the {@link RelatedItemIndexingMessage} the method
 * {@link RelatedItem#getRelatedItemPids()} returns the
 * related product ids
 */
public interface RelatedItemIndexingMessageConverter {

    /**
     * There is no guarantee of the ordering of the RelatedItem array is in the
     * same order as {@link RelatedItemSet#relatedItems}
     *
     * @param message
     * @return an array of RelatedItems
     */
    public RelatedItem[] convertFrom(RelatedItemIndexingMessage message);
}
