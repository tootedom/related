package org.greencheek.relatedproduct.domain.searching;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKeyFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 09:47
 * To change this template use File | Settings | File Templates.
 */
public abstract class SearchRequestLookupKeyTest {

    public abstract SearchRequestLookupKeyFactory getFactory();

    @Before
    public void setUp() {

    }

    @Test
    public void put100000EmptyStringSearchRequestLookupKeysInAMap() {
        Map<SearchRequestLookupKey,Integer> map = new HashMap<SearchRequestLookupKey,Integer>((int)Math.ceil(100000/0.75));
        for(int i=0;i<100000;i++) {
            map.put(getFactory().createSearchRequestLookupKey(""), 1);
        }

        assertEquals("Should only be one key in the map",1,map.size());
    }

    @Test
    public void testCollisions() {
        String[] traditionalJDK6StringWithClashingHashCodes =
                new String[]{"AaAaAaAa", "AaAaBBBB", "AaAaAaBB", "AaAaBBAa",
                "BBBBAaAa", "BBBBBBBB", "BBBBAaBB", "BBBBBBAa",
                "AaBBAaAa", "AaBBBBBB", "AaBBAaBB", "AaBBBBAa",
                "BBAaAaAa", "BBAaBBBB", "BBAaAaBB", "BBAaBBAa"};

        SearchRequestLookupKey[] keys = new SearchRequestLookupKey[traditionalJDK6StringWithClashingHashCodes.length];
        for(int i = 0;i<traditionalJDK6StringWithClashingHashCodes.length;i++) {
            keys[i] = getFactory().createSearchRequestLookupKey(traditionalJDK6StringWithClashingHashCodes[i]);
        }

        int collisions = 0;
        for(SearchRequestLookupKey key : keys) {
            for(SearchRequestLookupKey key2 : keys) {
                if(key != key2) {
                    if((key.hashCode()==key2.hashCode())) collisions++;
                }
            }
        }

        System.out.println("Number of collisions in common strings: " + collisions);
        assertTrue("There should not be more the "+((traditionalJDK6StringWithClashingHashCodes.length*traditionalJDK6StringWithClashingHashCodes.length)/4)+" collisions",collisions<((traditionalJDK6StringWithClashingHashCodes.length*traditionalJDK6StringWithClashingHashCodes.length)/4));
    }

    @Test
    public void hashCodesMatch() {
        SearchRequestLookupKeyFactory factory = getFactory();
        SearchRequestLookupKey key1 = factory.createSearchRequestLookupKey("bob");
        SearchRequestLookupKey key2 = factory.createSearchRequestLookupKey("bob");
        assertTrue("The same keys must match:", key1.hashCode() == key2.hashCode());
    }

    @Test
    public void keysAreEqual() {
        SearchRequestLookupKeyFactory factory = getFactory();
        SearchRequestLookupKey key1 = factory.createSearchRequestLookupKey("bob");
        SearchRequestLookupKey key2 = factory.createSearchRequestLookupKey("bob");
        assertTrue("The same keys must match:", key1.equals(key2));
    }

    @Test
    public void keysAreNotEqual() {
        SearchRequestLookupKeyFactory factory = getFactory();
        SearchRequestLookupKey key1 = factory.createSearchRequestLookupKey("bob");
        String key2 = "bob";
        assertFalse("The same keys, but different objects, must not match:", key1.equals(key2));
    }

    @Test
    public void generationOfSimilarKeys() {
        System.out.println(getFactory().createSearchRequestLookupKey("").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("\0").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("Ace").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("AceAce").hashCode());

        System.out.println(getFactory().createSearchRequestLookupKey("AceAce").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("AceAce").hashCode());



        long genbefore = System.nanoTime();
        ArrayList<SearchRequestLookupKey> x = listofstrings(11);
        long genafter = System.nanoTime();
        System.out.println("time to create: "+((genafter-genbefore)/1000000000.0) + "s");

        long bef = System.nanoTime();
        Map<SearchRequestLookupKey,Integer> myhash = new HashMap<SearchRequestLookupKey,Integer>(65536);
        for(SearchRequestLookupKey s : x)
            myhash.put(s,1);
        long aft = System.nanoTime();

        double worse =     (aft-bef)/1000000000.0;

        System.out.println("hash population took about "+worse+" s");
        assertTrue("Population of the hashmap, should be less than 11seconds",worse<11);


        System.out.println(myhash.size());
        System.out.println(myhash.get(x.get(1)));

        System.out.println(getFactory().createSearchRequestLookupKey("AceAceAceAceAceAceAceBDe").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("AceAceAceAceAceAceAceAce").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("AdFAceAceBEFBDeAceAdFAdF").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("AceAceAceAce").hashCode());
        System.out.println(getFactory().createSearchRequestLookupKey("Ace").equals(getFactory().createSearchRequestLookupKey("Ace")));
    }

    private ArrayList<SearchRequestLookupKey> listofstrings(int lengthdividedby3) {
        SearchRequestLookupKey[] allcollide = {
                getFactory().createSearchRequestLookupKey("Ace"),
                getFactory().createSearchRequestLookupKey("BDe"),
                getFactory().createSearchRequestLookupKey("AdF"),
                getFactory().createSearchRequestLookupKey("BEF")};
        ArrayList<SearchRequestLookupKey> al = new ArrayList<SearchRequestLookupKey>();
        al.add(getFactory().createSearchRequestLookupKey(""));
        if(lengthdividedby3 <= 0 ) return al;
        int mask = 0xFFFF;
        for(int k = 0; k<lengthdividedby3;++k) {
            ArrayList<SearchRequestLookupKey> oldarray = al;
            al = new ArrayList<SearchRequestLookupKey>();
            for(SearchRequestLookupKey s : oldarray)
                for(SearchRequestLookupKey t : allcollide)
                    al.add(getFactory().createSearchRequestLookupKey(s.toString()+t.toString()));
        }
        System.out.println("generated "+al.size()+" ASCII strings of length "+al.get(0).toString().length());
        int expectedhashcode = al.get(0).hashCode() & mask;
        int number = 0;
        for (SearchRequestLookupKey t: al)
            if((t.hashCode() & mask)== expectedhashcode) {
                number++;
            }
        assertTrue("Number of SearchRequestLookupKeys mathcing on first 16bit should be less than 2000",number<2001);
        System.out.println(""+number + "... and they all collide (on first 16 bits)");
        return al;


    }
}
