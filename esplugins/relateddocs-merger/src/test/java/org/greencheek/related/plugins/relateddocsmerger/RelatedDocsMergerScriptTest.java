package org.greencheek.related.plugins.relateddocsmerger;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by dominictootell on 02/03/2014.
 */
public class RelatedDocsMergerScriptTest {

    RelatedDocsMergerScript script;

    private static String md5 = "79054025255fb1a26e4bc422aef54eb4";
    private static String CUSTOM_COMPARE_KEY = "compare_key";

    RelatedDocsMergerScript customComparator;

    @Before
    public void setup() {
        MergerFactory factory = new MergerFactory();
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,md5);
        params.put("newParam","xxx");

        script = (RelatedDocsMergerScript)factory.newScript(params);


        Map<String,Object> params2 = new HashMap<String,Object>();
        params2.put(MergerFactory.COMPARATOR_PARAM,CUSTOM_COMPARE_KEY);
        params2.put(CUSTOM_COMPARE_KEY,md5);
        params2.put("newParam","xxx");

        customComparator = (RelatedDocsMergerScript)factory.newScript(params2);

    }

    @Test
    public void testScriptParametersAddToSourceWhenMD5IsDifferent() throws Exception {
        Map<String,Object> ctx = new HashMap<String,Object>();


        Map<String,Object> source = new HashMap<String,Object>();
        source.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,"xxxx");
        ctx.put("_source",source);

        script.setNextVar("ctx",ctx);

        script.run();

        assertTrue(source.containsKey("newParam"));
        assertEquals("xxx",source.get("newParam"));
        assertEquals(md5,source.get(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
    }

    @Test
    public void testScriptParametersNotAddedToSourceWhenMD5IsSame() throws Exception {
        Map<String,Object> ctx = new HashMap<String,Object>();


        Map<String,Object> source = new HashMap<String,Object>();
        source.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,md5);
        ctx.put("_source",source);

        script.setNextVar("ctx",ctx);

        script.run();

        assertFalse(source.containsKey("newParam"));
        assertTrue(ctx.containsKey("op"));
        assertEquals("none", ctx.get("op"));
        assertEquals(md5,source.get(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
    }

    @Test
    public void testWhenCTXNotPassed() throws Exception {
        Map<String,Object> ctx = new HashMap<String,Object>();


        Map<String,Object> source = new HashMap<String,Object>();
        source.put(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY,md5);
        ctx.put("_source",source);

        script.setNextVar("ctx2",ctx);

        script.run();

        assertFalse(source.containsKey("newParam"));
        assertEquals(md5,source.get(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
    } 

    @Test
    public void testScriptParametersAddToSourceWhenCustomComparatorIsDifferent() {
        Map<String,Object> ctx = new HashMap<String,Object>();


        Map<String,Object> source = new HashMap<String,Object>();
        source.put(CUSTOM_COMPARE_KEY,"xxxx");
        ctx.put("_source",source);

        customComparator.setNextVar("ctx",ctx);

        customComparator.run();

        assertTrue(source.containsKey("newParam"));
        assertEquals("xxx",source.get("newParam"));
        assertEquals(md5,source.get(CUSTOM_COMPARE_KEY));
    }

    @Test
    public void testScriptParametersAddToSourceWhenOriginalSourceContainsNoComparatorKey() {
        Map<String,Object> ctx = new HashMap<String,Object>();


        Map<String,Object> source = new HashMap<String,Object>();
        ctx.put("_source",source);

        script.setNextVar("ctx",ctx);

        script.run();

        assertTrue(source.containsKey("newParam"));
        assertEquals("xxx",source.get("newParam"));
        assertEquals(md5,source.get(MergerFactory.DEFAULT_COMPARATOR_STRING_KEY));
    }


}
