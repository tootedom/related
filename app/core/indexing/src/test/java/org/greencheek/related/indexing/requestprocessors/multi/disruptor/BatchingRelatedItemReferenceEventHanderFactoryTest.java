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
