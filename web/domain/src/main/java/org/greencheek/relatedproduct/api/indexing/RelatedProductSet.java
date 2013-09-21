package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSet {
    public int numberOfRelatedProducts;
    public final RelatedProductInfo[] relatedProducts;


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

    public int getNumberOfRelatedProducts() {
        return this.numberOfRelatedProducts;
    }


}
