package org.greencheek.relatedproduct.api.indexing;

import javolution.io.Struct;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductInfo extends Struct {
    public final Struct.UTF8String id = new UTF8String(SystemPropertiesConfiguration.RELATED_PRODUCT_ID_LENGTH);
    public final RelatedProductAdditionalProperties additionalProperties = inner(new RelatedProductAdditionalProperties());

}
