/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.domain.searching;


import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * Tests the FrequentlyRelatedSearchResult domain object works as expected
 */
public class FrequentlyRelatedSearchResultTest {

    @Test
    public void testResultCreation() {
        FrequentlyRelatedSearchResult result = new FrequentlyRelatedSearchResult("123456",10);

        assertEquals(10, result.getFrequency());
        assertEquals("123456",result.getRelatedItemId());
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

    @Test
    public void testToString() {
        FrequentlyRelatedSearchResult result = new FrequentlyRelatedSearchResult("123456",10);
        FrequentlyRelatedSearchResult result4 = new FrequentlyRelatedSearchResult("123456",11);

        assertEquals("10:123456",result.toString());
        assertEquals("11:123456",result4.toString());
    }
}
