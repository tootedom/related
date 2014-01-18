package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the factory will create RelatedProductIndexingMessage objects in
 * accordance with the set configuration.
 */
public class RelatedProductIndexingMessageFactoryTest {

    @After
    public void tearDown() {
        System.clearProperty(Configuration.PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES);
        System.clearProperty(Configuration.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST);

    }

    @Test
    public void testRelatedProductIndexingMessageWith4RelatedProducts() throws Exception {
        System.setProperty(Configuration.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,"4");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductIndexingMessageFactory factory = new RelatedProductIndexingMessageFactory(config);

        RelatedProductIndexingMessage message = factory.newInstance();

        assertEquals(4, message.getMaxNumberOfRelatedProductsAllowed());
    }

    @Test
    public void testRelatedProductIndexingMessage() {

        System.setProperty(Configuration.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,"2");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductIndexingMessageFactory factory = new RelatedProductIndexingMessageFactory(config);

        RelatedProductIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedProductsAllowed());

    }

    @Test
    public void testRelatedProductIndexingMessageRestrictsNumberOfProperties() {

        System.setProperty(Configuration.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,"2");
        System.setProperty(Configuration.PROPNAME_MAX_NO_OF_RELATED_PRODUCT_PROPERTES,"3");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductIndexingMessageFactory factory = new RelatedProductIndexingMessageFactory(config);

        RelatedProductIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedProductsAllowed());

        assertEquals(3, message.getIndexingMessageProperties().getMaxNumberOfAvailableProperties());

    }
}
