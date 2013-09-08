package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductInfoIdentifier;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductInfo {


    public final RelatedProductInfoIdentifier id;
    public final RelatedProductAdditionalProperties additionalProperties;

    public RelatedProductInfo(Configuration configuration) {
        id = new RelatedProductInfoIdentifier(configuration);
        additionalProperties = new RelatedProductAdditionalProperties(configuration,configuration.getMaxNumberOfRelatedProductProperties());

    }


    public RelatedProductInfoIdentifier getId() {
        return id;
    }

    public RelatedProductAdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }
}
