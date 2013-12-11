package org.greencheek.relatedproduct.api.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the factory will create RelatedProductIndexingMessage objects in
 * accordance with the set configuration.
 */
public class RelatedProductIndexingMessageFactoryTest {

    @Test
    public void testRelatedProductIndexingMessageWith4RelatedProducts() throws Exception {
        System.setProperty("related-product.max.number.related.products.per.product","4");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductIndexingMessageFactory factory = new RelatedProductIndexingMessageFactory(config);

        RelatedProductIndexingMessage message = factory.newInstance();

        assertEquals(4, message.getMaxNumberOfRelatedProductsAllowed());
    }

    @Test
    public void testRelatedProductIndexingMessage() {

        System.setProperty("related-product.max.number.related.products.per.product","2");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductIndexingMessageFactory factory = new RelatedProductIndexingMessageFactory(config);

        RelatedProductIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedProductsAllowed());

    }

    @Test
    public void testRelatedProductIndexingMessageRestrictsNumberOfProperties() {

        System.setProperty("related-product.max.number.related.products.per.product","2");
        System.setProperty("related-product.max.number.related.product.properties","3");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedProductIndexingMessageFactory factory = new RelatedProductIndexingMessageFactory(config);

        RelatedProductIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedProductsAllowed());

        assertEquals(3, message.getIndexingMessageProperties().getMaxNumberOfAvailableProperties());

    }
}
