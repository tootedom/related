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

package org.greencheek.related.api.searching;

import org.greencheek.related.api.searching.lookup.RelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class RelatedItemSearchTest {

    @Test
    public void testCopy() {
        Configuration c  = new SystemPropertiesConfiguration();
        RelatedItemSearch searchObject = new RelatedItemSearch(new SystemPropertiesConfiguration());

        searchObject.getAdditionalSearchCriteria().addProperty("key","value");
        searchObject.getAdditionalSearchCriteria().addProperty("request","response");
        searchObject.setRelatedItemId("I am a unique identifier");

        searchObject.setMaxResults(1);
        searchObject.setRelatedItemSearchType(RelatedItemSearchType.FREQUENTLY_RELATED_WITH);

        RelatedItemSearchLookupKeyGenerator factory = new KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(c,new SipHashSearchRequestLookupKeyFactory());
        factory.setSearchRequestLookupKeyOn(searchObject);

        RelatedItemSearch copy = searchObject.copy(c);

        assertNotSame(copy,searchObject);

        assertEquals(copy.getMaxResults(),searchObject.getMaxResults());
        assertEquals(copy.getRelatedItemId(),searchObject.getRelatedItemId());
        assertEquals(copy.getRelatedItemSearchType(),searchObject.getRelatedItemSearchType());
        assertEquals(copy.getLookupKey(),searchObject.getLookupKey());

        assertEquals(copy.getAdditionalSearchCriteria().getNumberOfProperties(),searchObject.getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals(copy.getAdditionalSearchCriteria().getPropertyName(0),searchObject.getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals(copy.getAdditionalSearchCriteria().getPropertyName(1),searchObject.getAdditionalSearchCriteria().getPropertyName(1));

        assertEquals(copy.getAdditionalSearchCriteria().getPropertyValue(0),searchObject.getAdditionalSearchCriteria().getPropertyValue(0));
        assertEquals(copy.getAdditionalSearchCriteria().getPropertyValue(1),searchObject.getAdditionalSearchCriteria().getPropertyValue(1));

        assertNotSame(copy.getRelatedContentIdentifier(),searchObject.getRelatedContentIdentifier());
        assertEquals(copy.getRelatedContentIdentifier().toString(),searchObject.getRelatedContentIdentifier().toString());
    }
}
