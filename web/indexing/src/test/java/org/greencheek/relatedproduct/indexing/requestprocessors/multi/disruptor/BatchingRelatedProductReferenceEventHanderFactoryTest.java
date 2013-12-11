package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 *
 */
public class BatchingRelatedProductReferenceEventHanderFactoryTest {

    TestRelatedProductStorageRepositoryFactory repo = new TestRelatedProductStorageRepositoryFactory();

    @Test
    public void testFactoryCreatesNewBatchingRelatedProductReferenceEventHander() {
        BatchingRelatedProductReferenceEventHanderFactory factory = new BatchingRelatedProductReferenceEventHanderFactory(new SystemPropertiesConfiguration(),repo,null);

        assertNotSame(factory.getHandler(),factory.getHandler());

        assertEquals(2,repo.getInvocationCount());
    }

    private class TestRelatedProductStorageRepositoryFactory implements  RelatedProductStorageRepositoryFactory {

        int i = 0;
        @Override
        public RelatedProductStorageRepository getRepository(Configuration configuration) {
            i++;
            return null;
        }

        public int getInvocationCount() {
            return i;
        }
    }
}
