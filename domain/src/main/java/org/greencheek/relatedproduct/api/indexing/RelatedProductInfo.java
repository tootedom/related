package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.api.RelatedProductInfoIdentifier;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Presents an individual product and it's associated attributes.
 * The number of associated attributes a product is allowed is defined at construction
 * time.  It is the callee's responsibility to make sure these limits are not exhausted.
 *
 * In the following json payload, this domain object represents a single item from the "products" array.
 * i.e. id: 10, type : "map"
 * <pre>
 * {
 *   "channel" : "uk",
 *   "site"    : "amazon",
 *   "products":[
 *               {
 *                "id"   : "10",
 *                "type" : "map"
 *               },
 *               {
 *                "id"   : "11",
 *                "type" : "compass"
 *               },
 *               {
 *                "id"   : "12",
 *                "type" : "torch"
 *               }
 *             ]
 *  }
 * </pre>
 */
public class RelatedProductInfo {


    public final RelatedProductInfoIdentifier id;
    public final RelatedProductAdditionalProperties additionalProperties;

    public RelatedProductInfo(Configuration configuration) {
        id = new RelatedProductInfoIdentifier(configuration);
        additionalProperties = new RelatedProductAdditionalProperties(configuration,configuration.getMaxNumberOfRelatedProductProperties());

    }

    public void setId(String idAsString) {
        this.id.setId(idAsString);
    }
    public RelatedProductInfoIdentifier getId() {
        return id;
    }

    public RelatedProductAdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }
}
