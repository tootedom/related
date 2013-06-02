package org.greencheek.relatedproduct.util.config;

import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
@Named
public class SystemPropertiesConfiguration implements Configuration {
    public final static short MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES = Short.valueOf(System.getProperty("related-product.max.number.related.product.properties", "10"));



    public final static short MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE = Short.valueOf(System.getProperty("related-product.max.number.related.products.per.product", "10"));
    public final static short RELATED_PRODUCT_ID_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.id.length", "36"));
    public final static String RELATED_PRODUCT_INVALID_ID_STRING = System.getProperty("related-product.related.product.invalid.id.string", "INVALID_ID");
    public final static int MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES = Integer.valueOf(System.getProperty("related-product.max.related.product.post.data.size.in.bytes","10240"));

    public final static short RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.additional.key.length", "30"));
    public final static short RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH = Short.valueOf(System.getProperty("related-product.related.product.additional.value.length", "30"));
    public final static int SIZE_OF_INDEX_REQUEST_QUEUE = Integer.valueOf(System.getProperty("related-product.size.of.index.request.queue", "2048"));



    @Override
    public short getMaxNumberOfRelatedProductProperties() {
        return MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES;
    }

    @Override
    public short getMaxNumberOfRelatedProductsPerPurchase() {
        return MAX_NUMBER_OF_RELATED_PRODUCTS_PER_PURCHASE;
    }

    @Override
    public short getRelatedProductIdLength() {
        return RELATED_PRODUCT_ID_LENGTH;
    }

    @Override
    public String getRelatedProductInvalidIdString() {
        return RELATED_PRODUCT_INVALID_ID_STRING;
    }

    @Override
    public int getMaxRelatedProductPostDataSizeInBytes() {
        return MAX_RELATED_PRODUCT_POST_DATA_SIZE_IN_BYTES;
    }

    @Override
    public short getRelatedProductAdditionalPropertyKeyLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH;
    }

    @Override
    public short getRelatedProductAdditionalPropertyValueLength() {
        return RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH;
    }

    @Override
    public int getSizeOfIndexRequestQueue() {
        return SIZE_OF_INDEX_REQUEST_QUEUE;
    }
}
