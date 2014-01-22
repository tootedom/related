package org.greencheek.related.api.indexing;

import org.greencheek.related.api.RelatedItemAdditionalProperties;
import org.greencheek.related.api.RelatedItemInfoIdentifier;
import org.greencheek.related.util.config.Configuration;

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
public class RelatedItemInfo {


    public final RelatedItemInfoIdentifier id;
    public final RelatedItemAdditionalProperties additionalProperties;

    public RelatedItemInfo(Configuration configuration) {
        id = new RelatedItemInfoIdentifier(configuration);
        additionalProperties = new RelatedItemAdditionalProperties(configuration,configuration.getMaxNumberOfRelatedItemProperties());

    }

    public void setId(String idAsString) {
        this.id.setId(idAsString);
    }
    public RelatedItemInfoIdentifier getId() {
        return id;
    }

    public RelatedItemAdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }
}
