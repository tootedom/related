package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 19:33
 */
public class BasicRelatedProductIndexingMessageConverter implements RelatedProductIndexingMessageConverter{

    private final Configuration configuration;

    public BasicRelatedProductIndexingMessageConverter(Configuration configuration) {
        this.configuration = configuration;
    }

    public RelatedProduct[] convertFrom(RelatedProductIndexingMessage message) {

        RelatedProductSet products = message.getRelatedProducts();
        int numberOfRelatedProducts = products.getNumberOfRelatedProducts();

        RelatedProduct[] relatedProducts = new RelatedProduct[numberOfRelatedProducts];

        RelatedProductInfo[][] idLists = relatedIds(products.getListOfRelatedProductInfomation(),products.getNumberOfRelatedProducts());

        int length = numberOfRelatedProducts-1;
        for(int i =0;i<numberOfRelatedProducts;i++) {
            RelatedProductInfo id = idLists[i][length];
            relatedProducts[i] = createRelatedProduct(message,id,idLists[i],message.getIndexingMessageProperties());
        }


        return relatedProducts;

    }

    private RelatedProduct createRelatedProduct(RelatedProductIndexingMessage message,RelatedProductInfo info,
                                                RelatedProductInfo[] ids,
                                                RelatedProductAdditionalProperties indexProperties) {
        RelatedProductAdditionalProperties properties = new RelatedProductAdditionalProperties(info.getAdditionalProperties(),indexProperties);

        int relatedIdLength = ids.length-1;
        char[][] relatedIds = new char[relatedIdLength][];
        for(int i=0;i<relatedIdLength;i++) {
            relatedIds[i] = ids[i].getId().duplicate();
        }

        return new RelatedProduct(info.getId().duplicate(),message.getUTCFormattedDate(),relatedIds,properties);
    }


    /**
     * Given a list of ids {"1","2","3","5"}
     *
     * The method returns list of ids, where each id, is returned
     * with a link to the other ids in the list that it is related to.
     *
     * for example:
     *
     * 1 -> 2, 3, 5
     * 2 -> 3, 5, 1
     * 3 -> 5, 1, 2
     * 5 -> 1, 2, 3
     *
     * The item that is related to the the other items, is the last element in the returned
     * array (X marks the spot below)
     *
     * i.e.
     *              X
     * [
     *   [ 2, 3, 5, 1 ],
     *   [ 3, 5, 1, 2 ],
     *   [ 5, 1, 2, 3 ],
     *   [ 1, 2, 3, 5 ]
     * ]
     *
     *
     *
     */
    public static RelatedProductInfo[][] relatedIds(RelatedProductInfo[] ids, int length) {
        int len = length;
        RelatedProductInfo[][] idSets = new RelatedProductInfo[len][len];
        for(int j = 0;j<len;j++) {
            idSets[0][j] = ids[j];
        }

        for(int i=1;i<len;i++) {
            int k=0;
            for(int j=i;j<len;j++) {
                idSets[i][k++] = ids[j];
            }
            for(int j=0;j<i;j++) {
                idSets[i][k++] = ids[j];
            }
        }

        return idSets;
    }

}
