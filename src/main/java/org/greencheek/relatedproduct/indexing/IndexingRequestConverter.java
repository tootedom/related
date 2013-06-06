package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public interface IndexingRequestConverter {
    public void convertRequestIntoIndexingMessage(RelatedProductIndexingMessage convertedTo,
                                                  short maxNumberOfAdditionalProperties);
}
