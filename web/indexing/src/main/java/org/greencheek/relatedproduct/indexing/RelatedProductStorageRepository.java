package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.api.indexing.RelatedProduct;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductStorageRepository {
    // Must not be asynchronous, The input list will not be a copy, but a passed reference
    public void store(RelatedProductStorageLocationMapper indexToMapper,  List<RelatedProduct> relatedProducts);
    public void shutdown();
}
