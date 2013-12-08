package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.domain.searching.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.domain.searching.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class RelatedProductSearchTest {

    @Test
    public void testCopy() {
        Configuration c  = new SystemPropertiesConfiguration();
        RelatedProductSearch searchObject = new RelatedProductSearch(new SystemPropertiesConfiguration());

        searchObject.getAdditionalSearchCriteria().addProperty("key","value");
        searchObject.getAdditionalSearchCriteria().addProperty("request","response");
        searchObject.setRelatedContentId("I am a unique identifier");

        searchObject.setMaxResults(1);
        searchObject.setValidMessage(true);
        searchObject.setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);

        RelatedProductSearchLookupKeyGenerator factory = new KeyFactoryBasedRelatedProductSearchLookupKeyGenerator(c,new SipHashSearchRequestLookupKeyFactory());
        factory.setSearchRequestLookupKeyOn(searchObject);

        RelatedProductSearch copy = searchObject.copy(c);

        assertNotSame(copy,searchObject);

        assertEquals(copy.isValidMessage(),searchObject.isValidMessage());
        assertEquals(copy.getMaxResults(),searchObject.getMaxResults());
        assertEquals(copy.getRelatedContentId(),searchObject.getRelatedContentId());
        assertEquals(copy.getRelatedProductSearchType(),searchObject.getRelatedProductSearchType());
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
