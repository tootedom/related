package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequestParameterValidatorLocatorTest {


    private final Configuration configuration = new SystemPropertiesConfiguration();
    private final SearchRequestParameterValidatorLocator validatorLookup = new MapBasedSearchRequestParameterValidatorLookup(configuration);

    @Test
    public void testFrequentlyRelatedWithReturnsAValidator() {
        assertNotNull(validatorLookup.getValidatorForType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH));
    }

    @Test
    public void testFrequentlyRelatedWithReturnsNoValidator() {
        assertNull(validatorLookup.getValidatorForType(RelatedProductSearchType.MOST_RECENTLY_RELATED_WITH));
    }
}
