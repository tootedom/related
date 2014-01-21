package org.greencheek.related.api.indexing;

import org.greencheek.related.util.config.Configuration;

/**
 * Represents a group of {@link RelatedItemInfo} objects
 */
public class RelatedItemSet {
    private int numberOfRelatedItems;
    private final RelatedItemInfo[] relatedItems;


    public RelatedItemSet(Configuration configuration) {
        int num = configuration.getMaxNumberOfRelatedItemsPerItem();
        relatedItems = new RelatedItemInfo[num];
        for(int i=0;i<num;i++) {
            relatedItems[i] = new RelatedItemInfo(configuration);
        }
    }

    public void setNumberOfRelatedItems(int numberOfRelatedItems) {
        this.numberOfRelatedItems = numberOfRelatedItems;
    }

    public RelatedItemInfo[] getListOfRelatedItemInfomation() {
        return this.relatedItems;
    }

    /**
     * performs no bounds checking.
     *
     * @param index
     * @return
     */
    public RelatedItemInfo getRelatedItemAtIndex(int index) {
       return relatedItems[index];
    }

    /**
     * checks that the requested index is within the bounds of the currently
     * actively set number of related products
     *
     * @param index
     * @return
     */
    public RelatedItemInfo getCheckedRelatedItemAtIndex(int index) {
        return ( index < numberOfRelatedItems) ? relatedItems[index] : null;

    }

    public int getMaxNumberOfRelatedItems() {
        return this.relatedItems.length;
    }

    public int getNumberOfRelatedItems() {
        return this.numberOfRelatedItems;
    }


}
