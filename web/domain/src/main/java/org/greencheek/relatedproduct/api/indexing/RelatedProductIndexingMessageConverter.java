package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.domain.RelatedProduct;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductIndexingMessageConverter {

    public Set<RelatedProduct> convertFrom(RelatedProductIndexingMessage message);
}
