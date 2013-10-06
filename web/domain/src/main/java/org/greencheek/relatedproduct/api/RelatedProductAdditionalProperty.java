package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductAdditionalProperty {

    public final int  maxNameLength;
    public final int  maxValueLength;
    public int nameLength;
    public int valueLength;
    public final char[] name;
    public final char[] value;

    private RelatedProductAdditionalProperty(char[] name, char[] value) {
        this.maxNameLength = name.length;
        this.maxValueLength = value.length;
        this.nameLength = name.length;
        this.valueLength = value.length;
        this.name = name;
        this.value = value;

    }

    public RelatedProductAdditionalProperty(Configuration configuration) {
        maxNameLength = configuration.getRelatedProductAdditionalPropertyKeyLength();
        maxValueLength = configuration.getRelatedProductAdditionalPropertyValueLength();
        name = new char[maxNameLength];
        value =  new char[maxValueLength];
    }

    public void setName(String s) {
        nameLength = Math.min(s.length(),maxNameLength);
//        Util.copyStringCharacterArray(s,name,nameLength,0);

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
//        Util.getUnsafe().copyMemory(this.name, Util.getCharArrayOffset(), copyTo, Util.getCharArrayOffset(), nameLength*2);

        System.arraycopy(this.name,0,copyTo,0,nameLength);
        return copyTo;
    }

    public char[] getValueCharArray() {
        return this.value;
    }

    public char[] getDuplicateValueCharArray() {
        char[] copyTo = new char[valueLength];
//        Util.getUnsafe().copyMemory(this.value, Util.getCharArrayOffset(), copyTo, Util.getCharArrayOffset(), valueLength<<1);

        System.arraycopy(this.value,0,copyTo,0,valueLength);
        return copyTo;
    }

    public String getName() {
        return new String(name,0,nameLength);
    }

    public void setValue(String s) {
        valueLength = Math.min(s.length(),maxValueLength);;
//        Util.copyStringCharacterArray(s,value,valueLength,0);

        s.getChars(0,valueLength,value,0);
    }

    public String getValue() {
        return new String(value,0,valueLength);
    }

    public void copyTo(RelatedProductAdditionalProperty copyTo) {
        copyTo.nameLength=this.nameLength;
        copyTo.valueLength=this.valueLength;
//        Util.getUnsafe().copyMemory(this.name, Util.getCharArrayOffset(), copyTo.name, Util.getCharArrayOffset(), nameLength<<1);
//        Util.getUnsafe().copyMemory(this.value, Util.getCharArrayOffset(), copyTo.value, Util.getCharArrayOffset(), valueLength<<1);

        System.arraycopy(this.name,0,copyTo.name,0,nameLength);
        System.arraycopy(this.value,0,copyTo.value,0,valueLength);


    }

    public RelatedProductAdditionalProperty trimAndClone() {
        return new RelatedProductAdditionalProperty(getDuplicateNameCharArray(),getDuplicateValueCharArray());
    }
}
