package org.greencheek.relatedproduct.api.searching.lookup;

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
public class DJBX33AWithTCRSearchRequestLookupKey implements SearchRequestLookupKey {
    private static final short RMAX=1024;
    private static final int[] rdata = new int[RMAX]; //just over 8k
    private static final int mask = RMAX-1;

    // Set up the TCR
    static {
        for(int i=0;i<RMAX;i++)
            rdata[i] = new Random().nextInt();
    }

    private final int hash;
    private final String key;

    public DJBX33AWithTCRSearchRequestLookupKey(String key) {
        this.key = key;
        hash = generateHashCode();
    }

    private int generateHashCode() {
        int h = hash;
        int len = key.length();
        if (h == 0 && len > 0) {
            int off = 0;
            char val[] = key.toCharArray();

            int i=len+1;
            while(--i !=0) {
                h = 33*h + (val[off++] ^ rdata[i & mask]);
            }
        }
        return h;
    }

    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(o instanceof DJBX33AWithTCRSearchRequestLookupKey) {
            return ((DJBX33AWithTCRSearchRequestLookupKey)o).key.equals(this.key);
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
