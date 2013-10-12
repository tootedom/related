package org.greencheek.relatedproduct.domain.searching;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 09:29
 *
 * Tests the FrequentlyRelatedSearchResult domain object works as expected
 */
public class FrequentlyRelatedSearchResultTest {

    @Test
    public void testResultCreation() {
        FrequentlyRelatedSearchResult result = new FrequentlyRelatedSearchResult("123456",10);

        assertEquals(10, result.getFrequency());
        assertEquals("123456",result.getRelatedProductId());
    }

    @Test
    public void testEquals() {
        FrequentlyRelatedSearchResult result = new FrequentlyRelatedSearchResult("123456",10);
        FrequentlyRelatedSearchResult result2 = new FrequentlyRelatedSearchResult("123456",10);
        FrequentlyRelatedSearchResult result3 = new FrequentlyRelatedSearchResult("123457",10);
        FrequentlyRelatedSearchResult result4 = new FrequentlyRelatedSearchResult("123456",11);

        assertTrue(result.equals(result2));
        assertTrue(result.hashCode() == result2.hashCode());


        assertFalse(result2.equals(result3));
        assertFalse(result2.equals(result4));
    }
}
