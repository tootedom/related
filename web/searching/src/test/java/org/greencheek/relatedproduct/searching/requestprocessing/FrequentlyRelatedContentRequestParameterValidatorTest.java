package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertSame;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class FrequentlyRelatedContentRequestParameterValidatorTest {


    private final Configuration configuration = new SystemPropertiesConfiguration();
    private final FrequentlyRelatedContentRequestParameterValidator validator = new FrequentlyRelatedContentRequestParameterValidator(new SystemPropertiesConfiguration());

    @Test
    public void testMissingIdParameterReturnsInvalidMessage() {
        ValidationMessage message = validator.validateParameters(new HashMap<String, String>());

        assertSame(message,validator.INVALID_ID_MESSAGE);
    }

    @Test
    public void testIdParameterReturnsValidMessage() {
        ValidationMessage message = validator.validateParameters(new HashMap<String, String>(){{ put(configuration.getKeyForFrequencyResultId(),"11111");}});

        assertSame(message,validator.VALID_ID_MESSAGE);
    }
}
