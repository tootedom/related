package org.greencheek.relatedproduct.util.config;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {
    public static final String APPLICATION_CONTEXT_ATTRIBUTE_NAME = "ApplicationContext";

    public short getMaxNumberOfRelatedProductProperties();
    public short getMaxNumberOfRelatedProductsPerPurchase();
    public short getRelatedProductIdLength();
    public String getRelatedProductInvalidIdString();
    public int getMaxRelatedProductPostDataSizeInBytes();
    public short getRelatedProductAdditionalPropertyKeyLength();
    public short getRelatedProductAdditionalPropertyValueLength();
    public int getSizeOfIndexRequestQueue();
}
