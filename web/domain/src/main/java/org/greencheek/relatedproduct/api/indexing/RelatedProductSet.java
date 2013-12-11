package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Represents a group of {@link RelatedProductInfo} objects
 */
public class RelatedProductSet {
    private int numberOfRelatedProducts;
    private final RelatedProductInfo[] relatedProducts;


    public RelatedProductSet(Configuration configuration) {
        int num = configuration.getMaxNumberOfRelatedProductsPerPurchase();
        relatedProducts = new RelatedProductInfo[num];
        for(int i=0;i<num;i++) {
            relatedProducts[i] = new RelatedProductInfo(configuration);
        }
    }

    public void setNumberOfRelatedProducts(int numberOfRelatedProducts) {
        this.numberOfRelatedProducts = numberOfRelatedProducts;
    }

    public RelatedProductInfo[] getListOfRelatedProductInfomation() {
        return this.relatedProducts;
    }

    /**
     * performs no bounds checking.
     *
     * @param index
     * @return
     */
    public RelatedProductInfo getRelatedProductAtIndex(int index) {
       return relatedProducts[index];
    }

    /**
     * checks that the requested index is within the bounds of the currently
     * actively set number of related products
     *
     * @param index
     * @return
     */
    public RelatedProductInfo getCheckedRelatedProductAtIndex(int index) {
        return ( index < numberOfRelatedProducts ) ? relatedProducts[index] : null;

    }

    public int getMaxNumberOfRelatedProducts() {
        return this.relatedProducts.length;
    }

    public int getNumberOfRelatedProducts() {
        return this.numberOfRelatedProducts;
    }


}
