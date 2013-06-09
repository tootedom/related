package org.greencheek.relatedproduct.api;

import javolution.io.Struct;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalProperty extends Struct {

    public final UTF8String name;
    public final UTF8String value;

    public RelatedProductAdditionalProperty(Configuration configuration) {
        name = new UTF8String(configuration.getRelatedProductAdditionalPropertyKeyLength());
        value =  new UTF8String(configuration.getRelatedProductAdditionalPropertyValueLength());
    }
}
