package org.greencheek.relatedproduct.api;

import javolution.io.Struct;
import org.greencheek.relatedproduct.util.config.Configuration;
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
    public final Signed16 numberOfProperties;
    public final RelatedProductAdditionalProperty[] additionalProperties;


    public RelatedProductAdditionalProperties(Configuration configuration, short maxNumOfProperties) {
        numberOfProperties = new Signed16();
        short num = maxNumOfProperties;
        RelatedProductAdditionalProperty[] additionalProperties = new RelatedProductAdditionalProperty[num];

        for(int i=0;i<num;i++) {
            additionalProperties[i] = new RelatedProductAdditionalProperty(configuration);
        }
        this.additionalProperties = array(additionalProperties);
    }

    public void convertTo(Map<String,String> properties) {
        short numberOfProps = numberOfProperties.get();
        while(numberOfProps--!=0) {
            RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
            properties.put(prop.name.get(),prop.value.get());
        }
    }


    public int getStringLength(Configuration configuration) {
        return getStringLength(numberOfProperties.get(),configuration);
    }

    private int getStringLength(int numberOfProps,Configuration configuration) {
        return (numberOfProps*(1+configuration.getRelatedProductAdditionalPropertyKeyLength()
                +configuration.getRelatedProductAdditionalPropertyValueLength())) + (numberOfProps) + 1;
    }

    public String toString(Configuration configuration) {
        short numberOfProps = numberOfProperties.get();

        if(numberOfProps>0) {
            int lengthOfString =  getStringLength(numberOfProps, configuration);
            StringBuilder string = new StringBuilder(lengthOfString);

            while(numberOfProps--!=0) {
                RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
                string.append(prop.name.get()).append('=').append(prop.value.get()).append('&');
            }

            string.deleteCharAt(lengthOfString-1);

            return string.toString();
        } else {
            return "";
        }
    }
}
