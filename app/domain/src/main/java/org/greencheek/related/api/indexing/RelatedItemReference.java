package org.greencheek.related.api.indexing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 27/08/2013
 * Time: 22:06
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemReference {

    public RelatedItem relatedItem;

    public void setReference(RelatedItem product) {
        this.relatedItem = product;
    }

    public RelatedItem getReference() {
        return this.relatedItem;
    }
}
