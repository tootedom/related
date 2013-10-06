package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalPropertiesBak {
    private int numberOfProperties = 0;
    private final int maxNameLength;
    private final int maxValueLength;

    private final char[] names;
    private final char[] values;

    private final int[] nameLengths;
    private final int[] valueLengths;



//    private final RelatedProductAdditionalProperty[] additionalProperties;


    public RelatedProductAdditionalPropertiesBak(RelatedProductAdditionalPropertiesBak... buildFrom) {
        this.numberOfProperties = 0;
//        int numberOfNames = 0;
//        int numberOfValues = 0;
//        int numberOfNameProps = 0;

        for(RelatedProductAdditionalPropertiesBak properties : buildFrom) {
            numberOfProperties+=properties.getNumberOfProperties();
//            numberOfNameProps+=properties.nameLengths.length;
//            numberOfNames+= properties.names.length;
//            numberOfValues+= properties.values.length;
        }

        this.maxNameLength = buildFrom[0].maxNameLength;
        this.maxValueLength = buildFrom[0].maxValueLength;

        this.names = new char[numberOfProperties*maxNameLength];
        this.values = new char[numberOfProperties*maxValueLength];
        this.nameLengths = new int[numberOfProperties];
        this.valueLengths = new int[numberOfProperties];


//        long namesOffset, valuesOffset;
//        namesOffset = valuesOffset= Util.getCharArrayOffset();
//        long nameLengthsOffset,valueLengthsOffset;
//        nameLengthsOffset = valueLengthsOffset = Util.getIntArrayOffset();
//
//        for(RelatedProductAdditionalProperties properties : buildFrom) {
//
//            int numberOfProperties = properties.getNumberOfProperties();
//            long propsNameSize = numberOfProperties*maxNameLength;
//            long propsValueSize = numberOfProperties*maxValueLength;
//
//            long namesBytes = propsNameSize*Util.getCharArrayScale();
//            long valuesBytes = propsValueSize*Util.getCharArrayScale();
//            long nameLengthBytes = numberOfProperties*Util.getIntArrayScale();
//            long valuesLengthBytes = nameLengthBytes;
//
////            long namesBytes = properties.names.length*Util.getCharArrayScale();
////            long valuesBytes = properties.values.length*Util.getCharArrayScale();
////            long nameLengthBytes = properties.nameLengths.length*Util.getIntArrayScale();
////            long valuesLengthBytes = properties.valueLengths.length*Util.getIntArrayScale();
//
//            Util.getUnsafe().copyMemory(properties.names,Util.getCharArrayOffset(),this.names,namesOffset,namesBytes);
//            Util.getUnsafe().copyMemory(properties.values,Util.getCharArrayOffset(),this.values,valuesOffset,valuesBytes);
//
//            Util.getUnsafe().copyMemory(properties.nameLengths,Util.getIntArrayOffset(),this.nameLengths,nameLengthsOffset,nameLengthBytes);
//            Util.getUnsafe().copyMemory(properties.valueLengths,Util.getIntArrayOffset(),this.valueLengths,valueLengthsOffset,valuesLengthBytes);
//            //         Util.getUnsafe().copyMemory(this.values, Util.getCharArrayOffset(), copy, Util.getCharArrayOffset(), valueLength*Util.getCharArrayScale());
//
//            namesOffset+=namesBytes;
//            valuesOffset+=valuesBytes;
//            nameLengthsOffset+=nameLengthBytes;
//            valueLengthsOffset+=valuesLengthBytes;
//
//        }


        int nameOffset, valueOffset;
        nameOffset = valueOffset = 0;

        int i=0;
        for(RelatedProductAdditionalPropertiesBak properties : buildFrom) {
            int numberOfProperties = properties.getNumberOfProperties();
            int namesLength = numberOfProperties*maxNameLength;
            int valuesLength = numberOfProperties*maxValueLength;

            System.arraycopy(properties.names,0,this.names,nameOffset,namesLength);
            System.arraycopy(properties.values,0,this.values,valueOffset,valuesLength);

            for(int j=0;j<numberOfProperties;j++) {
                nameLengths[i] = properties.nameLengths[j];
                valueLengths[i++] = properties.valueLengths[j];
            }

            nameOffset+=numberOfProperties*maxNameLength;
            valueOffset+=numberOfProperties*maxValueLength;
        }


//
//        additionalProperties = new RelatedProductAdditionalProperty[numberOfProperties];
//
//        int index = 0;
//        for(RelatedProductAdditionalProperties properties : buildFrom) {
//            for(int i=0;i<properties.getNumberOfProperties();i++) {
//                additionalProperties[index++] = properties.additionalProperties[i].trimAndClone();
//            }
//        }
    }

    public RelatedProductAdditionalPropertiesBak(Configuration configuration, int maxNumOfProperties) {
        this.maxNameLength = configuration.getRelatedProductAdditionalPropertyKeyLength();
        this.maxValueLength = configuration.getRelatedProductAdditionalPropertyValueLength();

        names = new char[this.maxNameLength * maxNumOfProperties];
        values = new char[this.maxValueLength * maxNumOfProperties];
        nameLengths = new int[maxNumOfProperties];
        valueLengths = new int[maxNumOfProperties];

//        additionalProperties = new RelatedProductAdditionalProperty[maxNumOfProperties];
//
//        for(int i=0;i<maxNumOfProperties;i++) {
//            additionalProperties[i] = new RelatedProductAdditionalProperty(configuration);
//        }
    }

    public void setNumberOfProperties(int numberOfProperties) {
        this.numberOfProperties = numberOfProperties;
    }

    public int getNumberOfProperties() {
        return this.numberOfProperties;
    }

//    public RelatedProductAdditionalProperty[] getAdditionalProperties() {
//        return this.additionalProperties;
//    }

    public void setProperty(String name, String value, int propertyIndex) {
        int nameLength = Math.min(name.length(),maxNameLength);

//        Util.copyStringCharacterArray(name,names,nameLength,propertyIndex*maxNameLength);

        name.getChars(0,nameLength,names,propertyIndex*maxNameLength);
        nameLengths[propertyIndex] = nameLength;

        int valueLength = Math.min(value.length(),maxValueLength);
//        Util.copyStringCharacterArray(value,values,valueLength,propertyIndex*maxValueLength);

        value.getChars(0, valueLength, values, propertyIndex * maxValueLength);
        valueLengths[propertyIndex] = valueLength;


//        additionalProperties[propertyIndex].setName(name);
//        additionalProperties[propertyIndex].setValue(value);
    }

    public String getPropertyName(int propertyIndex) {
        return new String(names,propertyIndex*maxNameLength,nameLengths[propertyIndex]);

//        return additionalProperties[propertyIndex].getName();
    }

    public String getPropertyValue(int propertyIndex) {
        return new String(values,propertyIndex*maxValueLength,valueLengths[propertyIndex]);

//        return additionalProperties[propertyIndex].getValue();
    }

    public char[] getPropertyValueCharArray(int propertyIndex) {
        int valueLength = valueLengths[propertyIndex];
        char[] copy = new char[valueLength];
        Util.getUnsafe().copyMemory(this.values, ((propertyIndex*maxValueLength)*Util.getCharArrayScale())+Util.getCharArrayOffset(), copy,Util.getCharArrayOffset(), valueLength*Util.getCharArrayScale());
        return copy;
    }


    public void copyTo(RelatedProductAdditionalProperties copyTo) {
//        copyTo.numberOfProperties = numberOfProperties;
//        copyTo.maxNameLength = maxNameLength;
//        copyTo.maxValueLength = maxValueLength;
//
//
//        RelatedProductAdditionalProperty[] copyToProperties = copyTo.additionalProperties;
//        RelatedProductAdditionalProperty[] srcToProperties = this.additionalProperties;
//        for(int i=0;i<numberOfProperties;i++) {
//            srcToProperties[i].copyTo(copyToProperties[i]);
//        }
    }

//    public RelatedProductAdditionalProperties clone() {
//        RelatedProductAdditionalProperties properties = new RelatedProductAdditionalProperties()
//    }

    public void convertTo(Map<String,String> properties) {
        int numberOfProps = numberOfProperties;
        while(numberOfProps--!=0) {
//            RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
            properties.put(getPropertyName(numberOfProps),getPropertyValue(numberOfProps));
        }
    }


//    /**
//     * Returns a two dimensional array that is like that of a map:
//     * [ key ],[ value ]
//     * [ key ],[ value ]
//     * [ key ],[ value ]
//     *
//     * @return
//     */
//    public String[][] convertToStringArray() {
//        int numberOfProps = numberOfProperties;
//        String[][] props = new String[numberOfProps][2];
//        while(numberOfProps--!=0) {
//            RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
//            props[numberOfProps][0] =  new String(prop.name,0,prop.nameLength);
//            props[numberOfProps][1] = new String(prop.value,0,prop.valueLength);
//        }
//        return props;
//    }

    public int getStringLength(Configuration configuration) {
        return getStringLength(numberOfProperties,configuration);
    }

    // TODO : Need to get the length from the properties
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
//                RelatedProductAdditionalProperty prop = additionalProperties[numberOfProps];
                string.append(names,numberOfProps*maxNameLength,nameLengths[numberOfProps]).append('=').append(values,numberOfProps*maxValueLength,valueLengths[numberOfProps]).append('&');
            }

            string.deleteCharAt(string.length()-1);

            return string.toString();
        } else {
            return "";
        }
    }
}
