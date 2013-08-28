package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalProperty {

    public final short  maxNameLength;
    public final short  maxValueLength;
    public short nameLength;
    public short valueLength;
    public final char[] name;
    public final char[] value;

    public RelatedProductAdditionalProperty(Configuration configuration) {
        maxNameLength = configuration.getRelatedProductAdditionalPropertyKeyLength();
        maxValueLength = configuration.getRelatedProductAdditionalPropertyValueLength();
        name = new char[maxNameLength];
        value =  new char[maxValueLength];
    }

    public void setName(String s) {
        char[] src = s.toCharArray();
        short min = (short)Math.min(src.length,maxNameLength);
        nameLength = min;
        System.arraycopy(src,0,name,0,min);
    }

    public String getName() {
        return new String(name,0,nameLength);
    }

    public void setValue(String s) {
        char[] src = s.toCharArray();
        short min = (short)Math.min(src.length,maxValueLength);
        valueLength = min;
        System.arraycopy(src,0,value,0,min);
    }

    public String getValue() {
        return new String(value,0,valueLength);
    }

    public void copyTo(RelatedProductAdditionalProperty copyTo) {
        copyTo.nameLength=this.nameLength;
        copyTo.valueLength=this.valueLength;
        System.arraycopy(this.name,0,copyTo.name,0,nameLength);
        System.arraycopy(this.value,0,copyTo.value,0,valueLength);


    }
}
