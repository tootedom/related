package org.greencheek.relatedproduct.api.indexing;

import javolution.io.Struct;
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

    public void copyInto(RelatedProductIndexingMessage copyTo) {
        copyTo.setValidMessage(this.validMessage);
        copyTo.setUTCFormattedDate(this.dateUTC);

        short numberOfRelatedProducts = relatedProducts.numberOfRelatedProducts;
        copyTo.relatedProducts.setNumberOfRelatedProducts(numberOfRelatedProducts);
        RelatedProductInfo[] sourceInfo = relatedProducts.relatedProducts;
        for(int i =0;i<numberOfRelatedProducts;i++) {
            RelatedProductInfo infoCopyFrom = sourceInfo[i];
            RelatedProductInfo infoCopyTo = copyTo.relatedProducts.relatedProducts[i];
            infoCopyFrom.id.copyTo(infoCopyTo.id);

            infoCopyFrom.additionalProperties.copyTo(infoCopyTo.additionalProperties);
        }

        short numberOfAdditionalProps = additionalProperties.getNumberOfProperties();

        copyTo.additionalProperties.setNumberOfProperties(additionalProperties.getNumberOfProperties());
        RelatedProductAdditionalProperty[] srcProps = additionalProperties.additionalProperties;
        RelatedProductAdditionalProperty[] targetProps = copyTo.additionalProperties.additionalProperties;
        for(int i=0;i<numberOfAdditionalProps;i++) {
            srcProps[i].copyTo(targetProps[i]);
        }

    }

    @Override
    public String toString()
    {
        return "RelatedProductIndexingMessage";
    }




}
