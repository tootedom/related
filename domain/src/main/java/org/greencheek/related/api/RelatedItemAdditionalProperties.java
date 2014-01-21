package org.greencheek.related.api;

import org.greencheek.related.util.config.Configuration;

import java.util.Map;

/**
 * Stores a list of properties (key/value pairs).
 * The object stores only a finite number of properties.
 * No bounds checking is performed by the properties object.
 *
 * The number of properties actually populated in the object can be less than
 * the maximum number of properties.
 * The method {@link RelatedItemAdditionalProperties#getNumberOfProperties()}
 * will return the number of properties actually stored in the object
 *
 * The object is a fixed size, so that memory it uses can be allocated upfront
 * via the disruptor.
 *
 * The key/value pairs are a finite size (characters).
 *
 */
public class RelatedItemAdditionalProperties {
    private int numberOfProperties = 0;
    private final RelatedItemAdditionalProperty[] additionalProperties;

    /**
     * Builds a properties object, that holds the given maximum number of properties.
     * The configuration is required to make sure that the property name and value are of a
     * set size.
     *
     * @param configuration
     * @param maxNumOfProperties
     */
    public RelatedItemAdditionalProperties(Configuration configuration, int maxNumOfProperties) {
        additionalProperties = new RelatedItemAdditionalProperty[maxNumOfProperties];

        for(int i=0;i<maxNumOfProperties;i++) {
            additionalProperties[i] = new RelatedItemAdditionalProperty(configuration);
        }

    }


    /**
     * Builds a properties object that is create/populated from
     * several other properties objects.  The number of properties that will be contained at the end is
     * a combination of the number of populated that were contained in the passed in
     * properties.
     *
     * @param buildFrom
     */
    public RelatedItemAdditionalProperties(RelatedItemAdditionalProperties... buildFrom) {
        this.numberOfProperties = 0;
        for(RelatedItemAdditionalProperties properties : buildFrom) {
            numberOfProperties+=properties.getNumberOfProperties();
        }

        additionalProperties = new RelatedItemAdditionalProperty[numberOfProperties];

        int index = 0;
        for(RelatedItemAdditionalProperties properties : buildFrom) {
            for(int i=0;i<properties.getNumberOfProperties();i++) {
                additionalProperties[index++] = properties.additionalProperties[i].trimAndClone();
            }
        }
    }


    public void setNumberOfProperties(int numberOfProperties) {
        this.numberOfProperties = numberOfProperties;
    }

    public int getNumberOfProperties() {
        return this.numberOfProperties;
    }

    /**
     * Returns the number of properties that are available to be set
     * @return
     */
    public int getMaxNumberOfAvailableProperties() {
        return additionalProperties.length;
    }

    /**
     * Sets the property, at the given index, to the given name, and value.
     *
     * @param name The property name
     * @param value the property value
     * @param propertyIndex the property that is to be set or written over.
     */
    public void setProperty(String name, String value, int propertyIndex) {
        additionalProperties[propertyIndex].setName(name);
        additionalProperties[propertyIndex].setValue(value);
    }

    public void addProperty(String name, String value) {
        setProperty(name,value,numberOfProperties++);
    }

    public String getPropertyName(int propertyIndex) {
        return additionalProperties[propertyIndex].getName();
    }

    public String getPropertyValue(int propertyIndex) {
        return additionalProperties[propertyIndex].getValue();
    }

    public char[] getPropertyValueCharArray(int propertyIndex) {
        return additionalProperties[propertyIndex].getValueCharArray();
    }


    /**
     * Copies the populated properties from the current object to the given object
     * The provided (copyTo) properties object (the one that is having properties copied to it),
     * should have same or more number of maximum properties
     *
     * @param copyTo
     */
    public void copyTo(RelatedItemAdditionalProperties copyTo) {
        copyTo.numberOfProperties = numberOfProperties;
        RelatedItemAdditionalProperty[] copyToProperties = copyTo.additionalProperties;
        RelatedItemAdditionalProperty[] srcToProperties = this.additionalProperties;
        for(int i=0;i<numberOfProperties;i++) {
            srcToProperties[i].copyTo(copyToProperties[i]);
        }
    }

    public void convertTo(Map<String,String> properties) {
        int numberOfProps = numberOfProperties;
        while(numberOfProps--!=0) {
            RelatedItemAdditionalProperty prop = additionalProperties[numberOfProps];
            properties.put(new String(prop.name,0,(int)prop.nameLength),new String(prop.value,0,prop.valueLength));
        }
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
            RelatedItemAdditionalProperty prop = additionalProperties[numberOfProps];
            props[numberOfProps][0] =  new String(prop.name,0,prop.nameLength);
            props[numberOfProps][1] = new String(prop.value,0,prop.valueLength);
        }
        return props;
    }

    public int getUrlQueryTypeStringLength() {
        int length = numberOfProperties + (numberOfProperties-1);
        for(int i=0;i<numberOfProperties;i++) {
            length+=additionalProperties[i].getNameLength() + additionalProperties[i].getValueLength();
        }
        return length;
    }


    public static int getMaxPotentialUrlQueryTypeStringLength(int numberOfProps, Configuration configuration) {
        return (numberOfProps*(1+configuration.getRelatedItemAdditionalPropertyKeyLength()
                +configuration.getRelatedItemAdditionalPropertyValueLength())) + (numberOfProps) + 1;
    }

    public String toUrlQueryTypeString() {


        if(getNumberOfProperties()>0) {
            int lengthOfString =  getUrlQueryTypeStringLength();
            StringBuilder string = new StringBuilder(lengthOfString);

            for(int i=0;i<getNumberOfProperties();i++) {
                RelatedItemAdditionalProperty prop = additionalProperties[i];
                string.append(prop.name,0,prop.nameLength).append('=').append(prop.value,0,prop.valueLength).append('&');
            }

            string.deleteCharAt(string.length()-1);

            return string.toString();
        } else {
            return "";
        }
    }


    /**
     * Represents a name and a value.  I.e. a key/value pair.
     */
    private final class RelatedItemAdditionalProperty {

        public final int maxNameLength;
        public final int maxValueLength;
        public int nameLength;
        public int valueLength;
        public final char[] name;
        public final char[] value;

        private RelatedItemAdditionalProperty(char[] name, char[] value) {
            this.maxNameLength = name.length;
            this.maxValueLength = value.length;
            this.nameLength = name.length;
            this.valueLength = value.length;
            this.name = name;
            this.value = value;

        }

        public RelatedItemAdditionalProperty(Configuration configuration) {
            maxNameLength = configuration.getRelatedItemAdditionalPropertyKeyLength();
            maxValueLength = configuration.getRelatedItemAdditionalPropertyValueLength();
            name = new char[maxNameLength];
            value =  new char[maxValueLength];
        }

        public void setName(String s) {
            nameLength = Math.min(s.length(),maxNameLength);
            s.getChars(0,nameLength,name,0);
        }

        public int getNameLength() {
            return nameLength;
        }

        public int getValueLength() {
            return valueLength;
        }

        public char[] getNameCharArray() {
            return name;
        }

        public char[] getDuplicateNameCharArray() {
            char[] copyTo = new char[nameLength];
            System.arraycopy(this.name,0,copyTo,0,nameLength);
            return copyTo;
        }

        public char[] getValueCharArray() {
            return this.value;
        }

        public char[] getDuplicateValueCharArray() {
            char[] copyTo = new char[valueLength];
            System.arraycopy(this.value,0,copyTo,0,valueLength);
            return copyTo;
        }

        public String getName() {
            return new String(name,0,nameLength);
        }

        public void setValue(String s) {
            valueLength = Math.min(s.length(),maxValueLength);
            s.getChars(0,valueLength,value,0);
        }

        public String getValue() {
            return new String(value,0,valueLength);
        }

        public void copyTo(RelatedItemAdditionalProperty copyTo) {
            copyTo.nameLength=this.nameLength;
            copyTo.valueLength=this.valueLength;

            System.arraycopy(this.name,0,copyTo.name,0,nameLength);
            System.arraycopy(this.value,0,copyTo.value,0,valueLength);
        }

        public RelatedItemAdditionalProperty trimAndClone() {
            return new RelatedItemAdditionalProperty(getDuplicateNameCharArray(),getDuplicateValueCharArray());
        }
    }

}
