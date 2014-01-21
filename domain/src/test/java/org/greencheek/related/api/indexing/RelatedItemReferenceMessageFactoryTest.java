package org.greencheek.related.api.indexing;

import com.lmax.disruptor.EventFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**

 */
public class RelatedItemReferenceMessageFactoryTest {

    @Test
    public void testCanCreateReferenceObject() {
        EventFactory<RelatedItemReference> factory = new RelatedItemReferenceMessageFactory();

        assertNotNull(factory.newInstance());

        assertTrue(factory.newInstance() instanceof RelatedItemReference);
    }
}
