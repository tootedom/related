package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.searching.domain.RelatedProduct;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductStorageRepository {
    public void store(RelatedProductStorageLocationMapper indexToMapper,  RelatedProduct... relatedProducts);
    public void shutdown();
}
