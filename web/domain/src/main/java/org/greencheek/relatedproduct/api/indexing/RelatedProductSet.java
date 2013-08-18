package org.greencheek.relatedproduct.api.indexing;

import javolution.io.Struct;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSet {
    public short numberOfRelatedProducts;
    public final RelatedProductInfo[] relatedProducts;


    public RelatedProductSet(Configuration configuration) {
        short num = configuration.getMaxNumberOfRelatedProductsPerPurchase();
        relatedProducts = new RelatedProductInfo[num];
        for(int i=0;i<num;i++) {
            relatedProducts[i] = new RelatedProductInfo(configuration);
        }
    }

    public void setNumberOfRelatedProducts(short numberOfRelatedProducts) {
        this.numberOfRelatedProducts = numberOfRelatedProducts;
    }

    public short getNumberOfRelatedProducts() {
        return this.numberOfRelatedProducts;
    }


}
