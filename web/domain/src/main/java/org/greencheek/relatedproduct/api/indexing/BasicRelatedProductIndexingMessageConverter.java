package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.searching.domain.RelatedProduct;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
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

    public Set<RelatedProduct> convertFrom(RelatedProductIndexingMessage message) {

        short numberOfRelatedProducts = message.relatedProducts.numberOfRelatedProducts.get();
        Set<RelatedProduct> relatedProducts = new HashSet<RelatedProduct>(numberOfRelatedProducts);
        List<RelatedProductInfo> productInfos = new ArrayList<RelatedProductInfo>(numberOfRelatedProducts);
        List<String> ids = new ArrayList<String>(numberOfRelatedProducts);

        getRelatedProductInfo(message,ids,productInfos);

        int length = numberOfRelatedProducts;
        while(length-- !=0) {
            RelatedProductInfo id = productInfos.get(length);
            Set<String> relatedIds = new HashSet<String>(ids);
            relatedIds.remove(id.id.get());
            Map<String,String> properties = new HashMap<String,String>(message.additionalProperties.numberOfProperties.get()+id.additionalProperties.numberOfProperties.get());
            id.additionalProperties.convertTo(properties);
            message.additionalProperties.convertTo(properties);

            relatedProducts.add(new RelatedProduct(id.id.get(),message.purchaseDate.get(),relatedIds,properties,configuration));
        }

        return relatedProducts;

    }

    private void getRelatedProductInfo(RelatedProductIndexingMessage message, List<String> ids, List<RelatedProductInfo> products ) {

        for(int i =0;i<message.relatedProducts.numberOfRelatedProducts.get();i++) {
            products.add(message.relatedProducts.relatedProducts[i]);
            ids.add(message.relatedProducts.relatedProducts[i].id.get());
        }

    }




}
