package org.greencheek.relatedproduct.searching.domain.searching;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 17:54
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequestLookupKeyTest {

    public static void main(String[] args) {
        System.out.println(new SearchRequestLookupKey("").hashCode());
        System.out.println(new SearchRequestLookupKey("\0").hashCode());
        System.out.println(new SearchRequestLookupKey("Ace").hashCode());
        System.out.println(new SearchRequestLookupKey("AceAce").hashCode());

        System.out.println(new SearchRequestLookupKey("AceAce").hashCode());
        System.out.println(new SearchRequestLookupKey("AceAce").hashCode());

        System.out.println(Integer.MAX_VALUE + " : " + ("" + Integer.MAX_VALUE).length());
        System.out.println(Integer.MIN_VALUE + " : " + ("" + Integer.MIN_VALUE).length());
        ArrayList<SearchRequestLookupKey> x = listofstrings(11);
        //System.out.println("strings:"+x);
        // now watch the hash table suffer!
        long bef = System.currentTimeMillis();
        Map<SearchRequestLookupKey,Integer> myhash = new HashMap<SearchRequestLookupKey,Integer>(65536);
        for(SearchRequestLookupKey s : x)
            myhash.put(s,1);
        long aft = System.currentTimeMillis();
        double worse =     (aft-bef)/1000.0;
        System.out.println("about "+worse+" s");

        System.out.println(myhash.size());
        System.out.println(myhash.get(x.get(1)));

        System.out.println(new SearchRequestLookupKey("AceAceAceAceAceAceAceBDe").hashCode());
        System.out.println(new SearchRequestLookupKey("AceAceAceAceAceAceAceAce").hashCode());
        System.out.println(new SearchRequestLookupKey("AdFAceAceBEFBDeAceAdFAdF").hashCode());
        System.out.println(new SearchRequestLookupKey("AceAceAceAce").hashCode());
        System.out.println(new SearchRequestLookupKey("Ace").equals(new SearchRequestLookupKey("Ace")));
    }

    public static ArrayList<SearchRequestLookupKey> listofstrings(int lengthdividedby3) {
        SearchRequestLookupKey[] allcollide = { new SearchRequestLookupKey("Ace"),new SearchRequestLookupKey("BDe"),new SearchRequestLookupKey("AdF"),new SearchRequestLookupKey("BEF")};
        ArrayList<SearchRequestLookupKey> al = new ArrayList<SearchRequestLookupKey>();
        al.add(new SearchRequestLookupKey(""));
        if(lengthdividedby3 <= 0 ) return al;
        int mask = 0xFFFF;
        for(int k = 0; k<lengthdividedby3;++k) {
            ArrayList<SearchRequestLookupKey> oldarray = al;
            al = new ArrayList<SearchRequestLookupKey>();
            for(SearchRequestLookupKey s : oldarray)
                for(SearchRequestLookupKey t : allcollide)
                    al.add(new SearchRequestLookupKey(s.toString()+t.toString()));
        }
        System.out.println("generated "+al.size()+" ASCII strings of length "+al.get(0).toString().length());
        int expectedhashcode = al.get(0).hashCode() & mask;
        int number = 0;
        for (SearchRequestLookupKey t: al)
            if((t.hashCode() & mask) == expectedhashcode) {
                number++;
                //System.out.println("matches: " + t.getString() +" " + al.get(0).getString());
            }
        System.out.println(""+number + "... and they all collide (on first 16 bits)");
        return al;


    }
}
