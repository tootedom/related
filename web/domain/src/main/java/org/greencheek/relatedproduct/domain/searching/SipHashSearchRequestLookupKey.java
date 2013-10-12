package org.greencheek.relatedproduct.domain.searching;

import com.github.emboss.siphash.SipHash;
import com.github.emboss.siphash.SipKey;
import com.github.emboss.siphash.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 12:09
 *
 * See:
 * https://devcentral.f5.com/weblogs/david/archive/2012/01/20/hashdos-ndash-the-post-of-doom-explained.aspx
 *
 * This is a implemention of the hash used there:
 * : DJBX33A, XORing random data to make target collision resistant
 */
public class SipHashSearchRequestLookupKey implements SearchRequestLookupKey {

    private static final SipKey SPEC_KEY;

    static {
        Random r = new Random();
        byte[] key = new byte[16];
        r.nextBytes(key);
        SPEC_KEY = new SipKey(key);
    }

    private final int hash;
    private final String key;

    public SipHashSearchRequestLookupKey(String key) {
        this.key = key;
        try {
            hash = generateHashCode(key.getBytes("UTF-8"));
        } catch(UnsupportedEncodingException e) {
            throw new InstantiationError("UTF-8 not found");
        }
    }

    private int generateHashCode(byte[] data) {
        return (int)SipHash.digest(SPEC_KEY,data);
    }

    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(o instanceof SipHashSearchRequestLookupKey) {
            return ((SipHashSearchRequestLookupKey)o).key.equals(this.key);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return hash;
    }

    public String toString() {
        return key;
    }


}
