package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 *
 */
public class BatchingRelatedItemReferenceEventHanderFactoryTest {

    TestRelatedItemStorageRepositoryFactory repo = new TestRelatedItemStorageRepositoryFactory();

    @Test
    public void testFactoryCreatesNewBatchingRelatedItemReferenceEventHander() {
        BatchingRelatedItemReferenceEventHanderFactory factory = new BatchingRelatedItemReferenceEventHanderFactory(new SystemPropertiesConfiguration(),repo,null);

        assertNotSame(factory.getHandler(),factory.getHandler());

        assertEquals(2,repo.getInvocationCount());
    }

    private class TestRelatedItemStorageRepositoryFactory implements RelatedItemStorageRepositoryFactory {

        int i = 0;
        @Override
        public RelatedItemStorageRepository getRepository(Configuration configuration) {
            i++;
            return null;
        }

        public int getInvocationCount() {
            return i;
        }
    }
}
