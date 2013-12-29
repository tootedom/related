package org.greencheek.relatedproduct.api.indexing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 27/08/2013
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductReference {

    public RelatedProduct relatedProduct;

    public void setReference(RelatedProduct product) {
        this.relatedProduct = product;
    }

    public RelatedProduct getReference() {
        return this.relatedProduct;
    }
}
