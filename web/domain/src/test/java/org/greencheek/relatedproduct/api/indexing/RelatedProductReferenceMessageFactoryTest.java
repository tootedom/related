package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.domain.RelatedProductReference;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**

 */
public class RelatedProductReferenceMessageFactoryTest {

    @Test
    public void testCanCreateReferenceObject() {
        EventFactory<RelatedProductReference> factory = new RelatedProductReferenceMessageFactory();

        assertNotNull(factory.newInstance());

        assertTrue(factory.newInstance() instanceof RelatedProductReference);
    }
}
