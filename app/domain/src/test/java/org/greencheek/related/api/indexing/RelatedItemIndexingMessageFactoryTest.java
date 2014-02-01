/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

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
        System.clearProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST);

    }

    @Test
    public void testRelatedItemIndexingMessageWith4RelatedItems() throws Exception {
        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST,"4");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemIndexingMessageFactory factory = new RelatedItemIndexingMessageFactory(config);

        RelatedItemIndexingMessage message = factory.newInstance();

        assertEquals(4, message.getMaxNumberOfRelatedItemsAllowed());
    }

    @Test
    public void testRelatedItemIndexingMessage() {

        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST,"2");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemIndexingMessageFactory factory = new RelatedItemIndexingMessageFactory(config);

        RelatedItemIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedItemsAllowed());

    }

    @Test
    public void testRelatedItemIndexingMessageRestrictsNumberOfProperties() {

        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_INDEX_REQUEST,"2");
        System.setProperty(PROPNAME_MAX_NO_OF_RELATED_ITEM_PROPERTES,"3");
        Configuration config = new SystemPropertiesConfiguration();

        RelatedItemIndexingMessageFactory factory = new RelatedItemIndexingMessageFactory(config);

        RelatedItemIndexingMessage message = factory.newInstance();

        assertEquals(2, message.getMaxNumberOfRelatedItemsAllowed());

        assertEquals(3, message.getIndexingMessageProperties().getMaxNumberOfAvailableProperties());

    }
}
