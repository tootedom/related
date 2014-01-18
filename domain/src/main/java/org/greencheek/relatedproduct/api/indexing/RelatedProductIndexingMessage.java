package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductIndexingMessage {

    private boolean validMessage;
    private String dateUTC;

    private final RelatedProductSet relatedProducts;
    private final RelatedProductAdditionalProperties additionalProperties;


    public RelatedProductIndexingMessage(Configuration config) {
        validMessage = false;
        relatedProducts = new RelatedProductSet(config);
        additionalProperties = new RelatedProductAdditionalProperties(config,config.getMaxNumberOfRelatedProductProperties());

    }

    public void setValidMessage(boolean isValid) {
        this.validMessage = isValid;
    }

    public void setUTCFormattedDate(String date) {
        this.dateUTC = date;
    }

    /**
     * This only returns set has been set via {@link #setUTCFormattedDate}
     * @return
     */
    public String getUTCFormattedDate() {
        return this.dateUTC;
    }

    public boolean isValidMessage() {
        return this.validMessage;
    }

    public int getMaxNumberOfRelatedProductsAllowed() {
        return this.relatedProducts.getMaxNumberOfRelatedProducts();
    }

    public RelatedProductSet getRelatedProducts() {
        return this.relatedProducts;
    }

    public RelatedProductAdditionalProperties getIndexingMessageProperties() {
        return this.additionalProperties;
    }

}
