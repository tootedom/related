package org.greencheek.relatedproduct.domain.searching;


import java.util.Random;

/**
 * https://devcentral.f5.com/weblogs/david/archive/2012/01/20/hashdos-ndash-the-post-of-doom-explained.aspx
 */
public class SearchRequestLookupKey {

    private static final short RMAX=2048;
    private static final int[] rdata = new int[RMAX]; //just over 8k

    // Set up the TCR
    static {
        for(int i=0;i<RMAX;i++)
            rdata[i] = new Random().nextInt();
    }

    private final int hash;
    private final String key;

    public SearchRequestLookupKey(String key) {
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
                h = 31*h + (val[off++] ^ rdata[i & (RMAX-1)]);
            }
        }
        return h;
    }

    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(o instanceof SearchRequestLookupKey) {
            return ((SearchRequestLookupKey)o).key.equals(this.key);
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
