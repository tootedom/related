package org.greencheek.related.plugins.relateddocsmerger;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by dominictootell on 02/03/2014.
 */
public class MergerFactoryTest {

    MergerFactory factory = new MergerFactory();

    @Test
    public void testMergerFactoryCreatedWithMD5Value() throws Exception {
        String value = "03938485747373772";
        Map<String,Object> withMd5 = new HashMap<String,Object>();
        withMd5.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,value);
        RelatedDocsMergerScript script = (RelatedDocsMergerScript)factory.newScript(withMd5);


        assertTrue(script.comparatorKey.equals(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
        assertTrue(script.comparatorValue.equals(value));
    }

    @Test
    public void testMergerFactoryCreatedWithComparatorValue() throws Exception {
        String value = "03938485747373772";
        String comparatorValue = "093344";
        Map<String,Object> withMd5 = new HashMap<String,Object>();
        withMd5.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,value);
        withMd5.put(MergerFactory.COMPARATOR_PARAM,"comparekey");
        withMd5.put("comparekey",comparatorValue);


        RelatedDocsMergerScript script = (RelatedDocsMergerScript)factory.newScript(withMd5);


        assertTrue(script.comparatorKey.equals("comparekey"));
        assertTrue(script.comparatorValue.equals(comparatorValue));
    }

    @Test
    public void testMergerFactoryCreatedWithNullMD5Value() throws Exception {
        String value = "03938485747373772";
        String comparatorValue = "093344";
        Map<String,Object> withMd5 = new HashMap<String,Object>();
        withMd5.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,null);



        RelatedDocsMergerScript script = (RelatedDocsMergerScript)factory.newScript(withMd5);


        assertTrue(script.comparatorKey.equals(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
        assertTrue(script.comparatorValue == null);
    }

    @Test
    public void testMergerFactoryCreatedWithNullComparatorValue() throws Exception {
        String value = "03938485747373772";
        String comparatorValue = "093344";
        Map<String,Object> withMd5 = new HashMap<String,Object>();
        withMd5.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,value);
        withMd5.put(MergerFactory.COMPARATOR_PARAM,"comparekey");
        withMd5.put("comparekey",null);


        RelatedDocsMergerScript script = (RelatedDocsMergerScript)factory.newScript(withMd5);


        assertTrue(script.comparatorKey.equals(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
        assertTrue(script.comparatorValue.equals("03938485747373772"));
    }

}
