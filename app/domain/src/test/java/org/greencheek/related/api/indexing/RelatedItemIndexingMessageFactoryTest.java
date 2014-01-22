package org.greencheek.related.api.indexing;

import static org.greencheek.related.util.config.ConfigurationConstants.*;

import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the factory will create RelatedItemIndexingMessage objects in
 * accordance with the set configuration.
 */
public class RelatedItemIndexingMessageFactoryTest {

    @After
    public void tearDown() {
        System.clearProperty(PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES);
        System.clearProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM);

    }

    @Test
    public void testRelatedItemIndexingMessageWith4RelatedItems() throws Exception {
        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM,"4");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemIndexingMessageFactory factory = new RelatedItemIndexingMessageFactory(config);

        RelatedItemIndexingMessage message = factory.newInstance();

        assertEquals(4, message.getMaxNumberOfRelatedItemsAllowed());
    }

    @Test
    public void testRelatedItemIndexingMessage() {

        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM,"2");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemIndexingMessageFactory factory = new RelatedItemIndexingMessageFactory(config);

        RelatedItemIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedItemsAllowed());

    }

    @Test
    public void testRelatedItemIndexingMessageRestrictsNumberOfProperties() {

        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM,"2");
        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES,"3");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemIndexingMessageFactory factory = new RelatedItemIndexingMessageFactory(config);

        RelatedItemIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedItemsAllowed());

        assertEquals(3, message.getIndexingMessageProperties().getMaxNumberOfAvailableProperties());

    }
}
