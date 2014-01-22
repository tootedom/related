package org.greencheek.related.api.indexing;

import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemIndexingMessage {

    private boolean validMessage;
    private String dateUTC;

    private final RelatedItemSet relatedItems;
    private final RelatedItemAdditionalProperties additionalProperties;


    public RelatedItemIndexingMessage(Configuration config) {
        validMessage = false;
        relatedItems = new RelatedItemSet(config);
        additionalProperties = new RelatedItemAdditionalProperties(config,config.getMaxNumberOfRelatedItemProperties());

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

    public int getMaxNumberOfRelatedItemsAllowed() {
        return this.relatedItems.getMaxNumberOfRelatedItems();
    }

    public RelatedItemSet getRelatedItems() {
        return this.relatedItems;
    }

    public RelatedItemAdditionalProperties getIndexingMessageProperties() {
        return this.additionalProperties;
    }

}
