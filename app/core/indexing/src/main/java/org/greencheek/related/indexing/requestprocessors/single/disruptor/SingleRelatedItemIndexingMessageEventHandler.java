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

package org.greencheek.related.indexing.requestprocessors.single.disruptor;

import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessageConverter;
import org.greencheek.related.api.indexing.RelatedItem;


import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepository;
import org.greencheek.related.indexing.requestprocessors.RelatedItemIndexingMessageEventHandler;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;


public class SingleRelatedItemIndexingMessageEventHandler implements RelatedItemIndexingMessageEventHandler {

    protected static final Logger log = LoggerFactory.getLogger(SingleRelatedItemIndexingMessageEventHandler.class);

    protected final RelatedItemIndexingMessageConverter indexConverter;
    protected final RelatedItemStorageRepository storageRepository;

    protected final List<RelatedItem> relatedItems;

    protected final RelatedItemStorageLocationMapper locationMapper;

    protected final int batchSize;

    private final int[] count = new int[30];
    private static final int COUNTER_POS = 14;

    private volatile boolean shutdown = false;

    public SingleRelatedItemIndexingMessageEventHandler(Configuration configuration,
                                                        RelatedItemIndexingMessageConverter converter,
                                                        RelatedItemStorageRepository repository,
                                                        RelatedItemStorageLocationMapper locationMapper)
    {
        this.indexConverter = converter;
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = configuration.getIndexBatchSize();
        this.count[COUNTER_POS] = batchSize;
        this.relatedItems = new ArrayList<RelatedItem>(batchSize + configuration.getMaxNumberOfRelatedItemsPerItem());
    }

    @Override
    public void onEvent(RelatedItemIndexingMessage request, long l, boolean endOfBatch) throws Exception {


        if(!request.isValidMessage()) {
            log.debug("Invalid indexing message.  Ignoring message");
            return;
        }
        if(request.getRelatedItems().getNumberOfRelatedItems()==0) {
            log.debug("Invalid indexing message, no related products.  Ignoring message");
            request.setValidMessage(false);
            return;
        }

        try {

            RelatedItem[] products = indexConverter.convertFrom(request);
            this.count[COUNTER_POS]-=products.length;
            for(RelatedItem p : products) {
                relatedItems.add(p);
            }

            if(endOfBatch || this.count[COUNTER_POS]<1) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
                    try {
                        storageRepository.store(locationMapper, relatedItems);
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}", products, e);
                    }
                }
                finally {
                    this.count[COUNTER_POS] = batchSize;
                    relatedItems.clear();
                }
            }
        } finally {
            request.setValidMessage(false);
        }


    }

    @Override
    public void shutdown() {
        if(!shutdown) {
            shutdown = true;
            try {
                storageRepository.shutdown();
            } catch(Exception e) {
                log.warn("Unable to shutdown respository client");
            }
        }
    }
}
