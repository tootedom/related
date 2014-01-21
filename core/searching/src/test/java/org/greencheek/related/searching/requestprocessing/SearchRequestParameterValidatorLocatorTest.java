package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
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
        assertNotNull(validatorLookup.getValidatorForType(RelatedItemSearchType.FREQUENTLY_RELATED_WITH));
    }

    @Test
    public void testFrequentlyRelatedWithReturnsNoValidator() {
        assertNull(validatorLookup.getValidatorForType(RelatedItemSearchType.MOST_RECENTLY_RELATED_WITH));
    }
}
