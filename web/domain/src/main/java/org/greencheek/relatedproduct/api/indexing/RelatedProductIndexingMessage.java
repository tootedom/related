package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductIndexingMessage {

    public boolean validMessage;
    public String dateUTC;

    public final RelatedProductSet relatedProducts;
    public final RelatedProductAdditionalProperties additionalProperties;


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

    public boolean isValidMessage() {
        return this.validMessage;
    }

    public RelatedProductSet getRelatedProducts() {
        return this.relatedProducts;
    }

    public RelatedProductAdditionalProperties getIndexingMessageProperties() {
        return this.additionalProperties;
    }

    @Override
    public String toString()
    {
        return "RelatedProductIndexingMessage";
    }




}
