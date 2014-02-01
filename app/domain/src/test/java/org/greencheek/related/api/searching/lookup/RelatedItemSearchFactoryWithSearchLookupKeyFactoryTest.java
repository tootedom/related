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

package org.greencheek.related.api.searching.lookup;

import org.greencheek.related.api.searching.KeyFactoryBasedRelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dominictootell on 05/01/2014.
 */
public class RelatedItemSearchFactoryWithSearchLookupKeyFactoryTest {

    RelatedItemSearchFactoryWithSearchLookupKeyFactory factory;
    RelatedItemSearchLookupKeyGenerator keyGenerator;
    Configuration config;

    @Before
    public void setUp() {
        config = new SystemPropertiesConfiguration();
        SearchRequestLookupKeyFactory keyFactory = new SipHashSearchRequestLookupKeyFactory();
        keyGenerator = new KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(config,keyFactory);

        factory = new RelatedItemSearchFactoryWithSearchLookupKeyFactory(config,keyGenerator);
    }

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
    }

    @Test
    public void testCreateSearchObject() throws Exception {
        assertNotNull(factory.createSearchObject());
    }

    @Test
    public void testNewInstance() throws Exception {
        assertNotNull(factory.newInstance());
    }


    private Map getProperties(String id, String[][] nameValuePairs) {
        Map properties = new HashMap();

        properties.put(config.getRequestParameterForId(),id);
        for(String[] nameValue : nameValuePairs) {
            Object obj = properties.get(nameValue[0]);
            if(obj==null) {
                properties.put(nameValue[0],nameValue[1]);
            } else {
                String[] newValue;
                if(obj instanceof String[]) {
                    String[] values = (String[])obj;
                    newValue = new String[values.length];
                    System.arraycopy(values,0,newValue,0,values.length);
                    newValue[values.length] = nameValue[1];
                } else {
                    newValue = new String[]{(String)obj,nameValue[1]};
                }
                properties.put(nameValue[0],newValue);
            }
        }

        return properties;
    }

    @Test
    public void testPopulateSearchObject() throws Exception {
        String[][] props = new String[4][2];
        props[0] = new String[]{"channel","one"};
        props[1] = new String[]{"type","computer"};
        props[2] = new String[]{"attributed_to","user1"};
        props[3] = new String[]{config.getRequestParameterForSize(),"3"};
        Map properties = getProperties("1",props);
        RelatedItemSearch objectToPopulate = factory.createSearchObject();
        assertEquals(0,objectToPopulate.getMaxResults());
        assertEquals("",objectToPopulate.getRelatedItemId());
        assertEquals(0,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        factory.populateSearchObject(objectToPopulate, RelatedItemSearchType.FREQUENTLY_RELATED_WITH,properties);

        assertEquals(3,objectToPopulate.getMaxResults());
        assertEquals("1",objectToPopulate.getRelatedItemId());
        assertEquals(3,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals("attributed_to",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals("user1",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(0));
        assertEquals("channel",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(1));
        assertEquals("one",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(1));
        assertEquals("type",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(2));
        assertEquals("computer",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(2));
    }

    @Test
    public void testPopulateSearchObjectCannotPopulateOverMaxAllowedSearchProperties() throws Exception {

        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT, "2");
        Configuration config = new SystemPropertiesConfiguration();
        factory = new RelatedItemSearchFactoryWithSearchLookupKeyFactory(config,keyGenerator);


        String[][] props = new String[4][2];
        props[0] = new String[]{"channel","one"};
        props[1] = new String[]{"type","computer"};
        props[2] = new String[]{"attributed_to","user1"};
        props[3] = new String[]{config.getRequestParameterForSize(),"3"};
        Map properties = getProperties("1",props);
        RelatedItemSearch objectToPopulate = factory.createSearchObject();
        assertEquals(0,objectToPopulate.getMaxResults());
        assertEquals("",objectToPopulate.getRelatedItemId());
        assertEquals(0,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        factory.populateSearchObject(objectToPopulate, RelatedItemSearchType.FREQUENTLY_RELATED_WITH,properties);

        assertEquals(3,objectToPopulate.getMaxResults());
        assertEquals("1",objectToPopulate.getRelatedItemId());
        assertEquals(2,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals("attributed_to",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals("user1",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(0));
        assertEquals("channel",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(1));
        assertEquals("one",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(1));
    } 
    @Test
    public void testInvalidRequestedSearchResults() {
        String[][] props = new String[4][2];
        props[0] = new String[]{"channel","one"};
        props[1] = new String[]{"type","computer"};
        props[2] = new String[]{"attributed_to","user1"};
        props[3] = new String[]{config.getRequestParameterForSize(),"fffff"};
        Map properties = getProperties("1",props);
        RelatedItemSearch objectToPopulate = factory.createSearchObject();
        assertEquals(0,objectToPopulate.getMaxResults());
        assertEquals("",objectToPopulate.getRelatedItemId());
        assertEquals(0,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        factory.populateSearchObject(objectToPopulate, RelatedItemSearchType.FREQUENTLY_RELATED_WITH,properties);

        assertEquals(config.getDefaultNumberOfResults(),objectToPopulate.getMaxResults());
        assertEquals("1",objectToPopulate.getRelatedItemId());
        assertEquals(3,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals("attributed_to",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals("user1",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(0));
        assertEquals("channel",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(1));
        assertEquals("one",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(1));
        assertEquals("type",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(2));
        assertEquals("computer",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(2));
    }
}
