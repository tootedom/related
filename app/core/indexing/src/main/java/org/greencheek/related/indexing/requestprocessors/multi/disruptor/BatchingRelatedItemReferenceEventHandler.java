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

import org.greencheek.related.api.indexing.RelatedItem;


import org.greencheek.related.api.indexing.RelatedItemReference;
import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

/**
 *
 */
public class BatchingRelatedItemReferenceEventHandler implements RelatedItemReferenceEventHandler {

    protected static final Logger log = LoggerFactory.getLogger(BatchingRelatedItemReferenceEventHandler.class);

    protected final RelatedItemStorageRepository storageRepository;

    protected final List<RelatedItem> relatedItems;

    protected final RelatedItemStorageLocationMapper locationMapper;

    protected final int batchSize;

    private final int[] count = new int[30];
    private static final int COUNTER_POS = 14;

    public BatchingRelatedItemReferenceEventHandler(int batchSize,
                                                    RelatedItemStorageRepository repository,
                                                    RelatedItemStorageLocationMapper locationMapper)
    {
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = batchSize;
        this.count[COUNTER_POS] = batchSize;
        this.relatedItems = new ArrayList<RelatedItem>(batchSize);
    }

    @Override
    public void onEvent(RelatedItemReference request, long l, boolean endOfBatch) throws Exception {

        try {
            relatedItems.add(request.getReference());

            if(endOfBatch || --this.count[COUNTER_POS] == 0) {
                try {
                    log.debug("Sending {} indexing requests to the storage repository",relatedItems.size());
                    try {
                        storageRepository.store(locationMapper, relatedItems);
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}", relatedItems,e);
                    }
                }
                finally {
                    this.count[COUNTER_POS]  = batchSize;
                    relatedItems.clear();
                }
            }
        } finally {
          request.setReference(null);
        }
    }

    public void shutdown() {
        try {
            storageRepository.shutdown();
        } catch(Exception e) {
            log.error("Problem shutting down storage repository");
        }
    }

}
