package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.config.Configuration;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 05/07/2013
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductInfoIdentifier {

    private static final String encoding = "UTF-8";
    private final short maxStringIdLength;
    private final short maxLength;
    private final byte[] id;
    private short length = 0;

    public RelatedProductInfoIdentifier(Configuration configuration) {
        maxStringIdLength = configuration.getRelatedProductIdLength();
        maxLength = (short)(maxStringIdLength*4);
        id = new byte[maxLength];
    }

    public void setId(String id) {
        String subString = id.substring(0, Math.min(id.length(),maxStringIdLength));
        try {
            setId(subString.getBytes(encoding));
        } catch(UnsupportedEncodingException e) {
            setId(subString.getBytes());
        }

    }

    public void setId(byte[] id) {
        short idLength = (short)Math.min(maxLength,id.length);
        length = idLength;
        System.arraycopy(id,0,this.id,0,idLength);
    }

    public void copyTo(RelatedProductInfoIdentifier id) {
        System.arraycopy(this.id,0,id.id,0,length);
        id.length = this.length;
    }


    public String toString() {
        try {
            return new String(id,0,length,encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(id,0,length);
        }
    }

}
