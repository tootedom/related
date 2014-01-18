package org.greencheek.relatedproduct.api.searching.lookup;

import org.greencheek.relatedproduct.api.searching.KeyFactoryBasedRelatedProductSearchLookupKeyGenerator;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
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
public class RelatedProductSearchFactoryWithSearchLookupKeyFactoryTest {

    RelatedProductSearchFactoryWithSearchLookupKeyFactory factory;
    RelatedProductSearchLookupKeyGenerator keyGenerator;
    Configuration config;

    @Before
    public void setUp() {
        config = new SystemPropertiesConfiguration();
        SearchRequestLookupKeyFactory keyFactory = new SipHashSearchRequestLookupKeyFactory();
        keyGenerator = new KeyFactoryBasedRelatedProductSearchLookupKeyGenerator(config,keyFactory);

        factory = new RelatedProductSearchFactoryWithSearchLookupKeyFactory(config,keyGenerator);
    }

    @After
    public void tearDown() {
        System.clearProperty(Configuration.PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT);
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
        RelatedProductSearch objectToPopulate = factory.createSearchObject();
        assertEquals(0,objectToPopulate.getMaxResults());
        assertEquals("",objectToPopulate.getRelatedContentId());
        assertEquals(0,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        factory.populateSearchObject(objectToPopulate, RelatedProductSearchType.FREQUENTLY_RELATED_WITH,properties);

        assertEquals(3,objectToPopulate.getMaxResults());
        assertEquals("1",objectToPopulate.getRelatedContentId());
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

        System.setProperty(Configuration.PROPNAME_MAX_NUMBER_OF_SEARCH_CRITERIA_FOR_RELATED_CONTENT, "2");
        Configuration config = new SystemPropertiesConfiguration();
        factory = new RelatedProductSearchFactoryWithSearchLookupKeyFactory(config,keyGenerator);


        String[][] props = new String[4][2];
        props[0] = new String[]{"channel","one"};
        props[1] = new String[]{"type","computer"};
        props[2] = new String[]{"attributed_to","user1"};
        props[3] = new String[]{config.getRequestParameterForSize(),"3"};
        Map properties = getProperties("1",props);
        RelatedProductSearch objectToPopulate = factory.createSearchObject();
        assertEquals(0,objectToPopulate.getMaxResults());
        assertEquals("",objectToPopulate.getRelatedContentId());
        assertEquals(0,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        factory.populateSearchObject(objectToPopulate, RelatedProductSearchType.FREQUENTLY_RELATED_WITH,properties);

        assertEquals(3,objectToPopulate.getMaxResults());
        assertEquals("1",objectToPopulate.getRelatedContentId());
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
        RelatedProductSearch objectToPopulate = factory.createSearchObject();
        assertEquals(0,objectToPopulate.getMaxResults());
        assertEquals("",objectToPopulate.getRelatedContentId());
        assertEquals(0,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        factory.populateSearchObject(objectToPopulate, RelatedProductSearchType.FREQUENTLY_RELATED_WITH,properties);

        assertEquals(config.getDefaultNumberOfResults(),objectToPopulate.getMaxResults());
        assertEquals("1",objectToPopulate.getRelatedContentId());
        assertEquals(3,objectToPopulate.getAdditionalSearchCriteria().getNumberOfProperties());

        assertEquals("attributed_to",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(0));
        assertEquals("user1",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(0));
        assertEquals("channel",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(1));
        assertEquals("one",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(1));
        assertEquals("type",objectToPopulate.getAdditionalSearchCriteria().getPropertyName(2));
        assertEquals("computer",objectToPopulate.getAdditionalSearchCriteria().getPropertyValue(2));
    }
}
