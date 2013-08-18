package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
public class BasicRelatedProductIndexingMessageConverter implements RelatedProductIndexingMessageConverter{

    private final Configuration configuration;

    public BasicRelatedProductIndexingMessageConverter(Configuration configuration) {
        this.configuration = configuration;
    }

    public RelatedProduct[] convertFrom(RelatedProductIndexingMessage message) {

        short numberOfRelatedProducts = message.relatedProducts.numberOfRelatedProducts;

        RelatedProduct[] relatedProducts = new RelatedProduct[numberOfRelatedProducts];
        String[] ids = new String[numberOfRelatedProducts];
        RelatedProductInfo[] productInfos = new RelatedProductInfo[numberOfRelatedProducts];

        getRelatedProductInfo(message.relatedProducts.relatedProducts,ids,productInfos,numberOfRelatedProducts);


        String[][] idLists = relatedIds(ids);
        int length = numberOfRelatedProducts-1;
        String[][] indexProperties = message.additionalProperties.convertToStringArray();
        for(int i =0;i<length;i++) {
            RelatedProductInfo id = productInfos[i];
            relatedProducts[i] = createRelatedProduct(message,id,idLists[i+1],indexProperties);
        }


        relatedProducts[length] = createRelatedProduct(message,productInfos[length],
                                                  idLists[0],indexProperties);
        return relatedProducts;

    }

    private RelatedProduct createRelatedProduct(RelatedProductIndexingMessage message,RelatedProductInfo info,
                                                String[] ids,
                                                String[][] indexProperties) {
        String[][] productProperties = info.additionalProperties.convertToStringArray();

        int indexSize = productProperties.length+indexProperties.length;
        String[][] additionalProperties = new String[indexSize--][2];

        for(int i=0;i<indexProperties.length;i++) {
            additionalProperties[indexSize][0]    = indexProperties[i][0];
            additionalProperties[indexSize--][1]  = indexProperties[i][1];
        }

        for(int i=0;i<productProperties.length;i++) {
            additionalProperties[indexSize][0]    = productProperties[i][0];
            additionalProperties[indexSize--][1]  = productProperties[i][1];
        }

        return new RelatedProduct(info.id.toString(),message.dateUTC,ids,additionalProperties);
    }

    public static String[][] relatedIds(String[] ids) {
        int len = ids.length;
        int lenMinOne = len-1;
        String[][] idSets = new String[len][lenMinOne];

        for(int i=0;i<len;i++) {
            int start = i;
            for(int j = 0;j<lenMinOne;j++) {
                int elem = start++;
                if(elem>lenMinOne) {
                    elem-=len;
                }
                idSets[i][j] = ids[elem];
            }
        }

        return idSets;
    }



    private void getRelatedProductInfo(RelatedProductInfo[] message, String[] ids, RelatedProductInfo[] products, short numberOfRelatedProducts ) {

        for(int i =0;i<numberOfRelatedProducts;i++) {
            products[i] = message[i];
            ids[i] = message[i].id.toString();
        }

    }





}
