package org.greencheek.relatedproduct.api.indexing;

import javolution.io.Struct;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalProperty extends Struct {

    public final UTF8String name = new UTF8String(SystemPropertiesConfiguration.RELATED_PRODUCT_ADDITIONAL_PROPERTY_KEY_LENGTH);
    public final UTF8String value = new UTF8String(SystemPropertiesConfiguration.RELATED_PRODUCT_ADDITIONAL_PROPERTY_VALUE_LENGTH);


}
