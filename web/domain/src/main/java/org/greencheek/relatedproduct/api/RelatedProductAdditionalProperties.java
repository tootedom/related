package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalProperties {
    public int numberOfProperties = 0;
    public final RelatedProductAdditionalProperty[] additionalProperties;


    public RelatedProductAdditionalProperties(Configuration configuration, short maxNumOfProperties) {
        short num = maxNumOfProperties;
        additionalProperties = new RelatedProductAdditionalProperty[num];

        for(int i=0;i<num;i++) {
            additionalProperties[i] = new RelatedProductAdditionalProperty(configuration);
        }
    }

    public void setNumberOfProperties(short numberOfProperties) {
        this.numberOfProperties = numberOfProperties;
    }

    public int getNumberOfProperties() {
        return this.numberOfProperties;
    }

    public RelatedProductAdditionalProperty[] getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void copyTo(RelatedProductAdditionalProperties copyTo) {
        copyTo.numberOfProperties = numberOfProperties;
        RelatedProductAdditionalProperty[] copyToProperties = copyTo.additionalProperties;
        RelatedProductAdditionalProperty[] srcToProperties = this.additionalProperties;
        for(int i=0;i<numberOfProperties;i++) {
            srcToProperties[i].copyTo(copyToProperties[i]);
        }
    }

    public void convertTo(Map<String,String> properties) {
        int numberOfProps = numberOfProperties;
        while(numberOfProps--!=0) {
            RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
            properties.put(new String(prop.name,0,(int)prop.nameLength),new String(prop.value,0,prop.valueLength));
        }
    }

    public static RelatedProductAdditionalProperty[] convertFrom(Configuration config, Map<String,String> propertes) {
        RelatedProductAdditionalProperty[] propArrays = new RelatedProductAdditionalProperty[propertes.size()];

        int i=0;
        for(Map.Entry<String,String> entry : propertes.entrySet() ) {
            propArrays[i] = new RelatedProductAdditionalProperty(config);
            propArrays[i].setName(entry.getKey());
            propArrays[i++].setValue(entry.getValue());
        }
        return propArrays;
    }

    /**
     * Returns a two dimensional array that is like that of a map:
     * [ key ],[ value ]
     * [ key ],[ value ]
     * [ key ],[ value ]
     *
     * @return
     */
    public String[][] convertToStringArray() {
        int numberOfProps = numberOfProperties;
        String[][] props = new String[numberOfProps][2];
        while(numberOfProps--!=0) {
            RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
            props[numberOfProps][0] =  new String(prop.name,0,prop.nameLength);
            props[numberOfProps][1] = new String(prop.value,0,prop.valueLength);
        }
        return props;
    }

    public int getStringLength(Configuration configuration) {
        return getStringLength(numberOfProperties,configuration);
    }

    public static int getStringLength(int numberOfProps,Configuration configuration) {
        return (numberOfProps*(1+configuration.getRelatedProductAdditionalPropertyKeyLength()
                +configuration.getRelatedProductAdditionalPropertyValueLength())) + (numberOfProps) + 1;
    }

    public String toString(Configuration config) {
        int numberOfProps = numberOfProperties;

        if(numberOfProps>0) {
            int lengthOfString =  getStringLength(numberOfProps,config);
            StringBuilder string = new StringBuilder(lengthOfString);

            while(numberOfProps--!=0) {
                RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
                string.append(prop.name,0,prop.nameLength).append('=').append(prop.value,0,prop.valueLength).append('&');
            }

            string.deleteCharAt(string.length()-1);

            return string.toString();
        } else {
            return "";
        }
    }
}
