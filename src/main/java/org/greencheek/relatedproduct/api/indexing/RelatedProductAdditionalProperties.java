package org.greencheek.relatedproduct.api.indexing;

import javolution.io.Struct;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalProperties extends Struct {
    public final Signed16 numberOfProperties = new Signed16();
    public final RelatedProductAdditionalProperty[] additionalProperties = array(new RelatedProductAdditionalProperty[SystemPropertiesConfiguration.MAX_NUMBER_OF_RELATED_PRODUCT_PROPERTIES]);

    public void convertTo(Map<String,String> properties) {
        short numberOfProps = numberOfProperties.get();
        while(numberOfProps--!=0) {
            RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
            properties.put(prop.name.get(),prop.value.get());
        }
    }


}
