package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 05/07/2013
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */

public class RelatedProductInfoIdentifier {

    protected final int maxStringIdLength;
    protected final char[] id;
    protected int length = 0;

    public RelatedProductInfoIdentifier(Configuration configuration) {
        maxStringIdLength = configuration.getRelatedProductIdLength();
        id = new char[maxStringIdLength];
    }

    public void setId(String id) {
        length = Math.min(id.length(),maxStringIdLength);
        id.getChars(0, length,this.id,0);
    }

    public char[] getIdCharArray() {
        return id;
    }

    public char[] duplicate() {
        char destination[] = new char[length];
        System.arraycopy(this.id,0,destination,0,length);
        return destination;
    }

    public String toString() {
        return new String(id,0,length);
    }


    public void setLength(int length) {
        this.length = length;
    }

    public void copyTo(RelatedProductInfoIdentifier destination) {
        System.arraycopy(this.id,0,destination.getIdCharArray(),0,length);
        destination.setLength(this.length);
    }
}