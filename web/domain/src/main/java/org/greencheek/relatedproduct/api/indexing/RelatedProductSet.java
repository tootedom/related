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
public class RelatedProductSet extends Struct {
    public final Signed16 numberOfRelatedProducts;
    public final RelatedProductInfo[] relatedProducts;


    public RelatedProductSet(Configuration configuration) {
        numberOfRelatedProducts = new Signed16();
        short num = configuration.getMaxNumberOfRelatedProductsPerPurchase();
        RelatedProductInfo[] relatedProductInfos = new RelatedProductInfo[num];
        for(int i=0;i<num;i++) {
            relatedProductInfos[i] = new RelatedProductInfo(configuration);
        }
        relatedProducts = array(relatedProductInfos);

    }

}
