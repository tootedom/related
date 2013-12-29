package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.api.searching.lookup.RelatedProductSearchLookupKeyGenerator;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKeyFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class KeyFactoryBasedRelatedProductSearchLookupKeyGeneratorTest extends RelatedProductSearchLookupKeyGeneratorTest{

    private final Configuration configuration = new SystemPropertiesConfiguration();
    @Override
    public RelatedProductSearchLookupKeyGenerator getGenerator() {
        return new KeyFactoryBasedRelatedProductSearchLookupKeyGenerator(configuration, new SipHashSearchRequestLookupKeyFactory());
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
