package org.greencheek.related.api.searching;

import org.greencheek.related.api.searching.lookup.RelatedItemSearchLookupKeyGenerator;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class KeyFactoryBasedRelatedItemSearchLookupKeyGeneratorTest extends RelatedItemSearchLookupKeyGeneratorTest {

    private final Configuration configuration = new SystemPropertiesConfiguration();
    @Override
    public RelatedItemSearchLookupKeyGenerator getGenerator() {
        return new KeyFactoryBasedRelatedItemSearchLookupKeyGenerator(configuration, new SipHashSearchRequestLookupKeyFactory());
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
