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

            if(endOfBatch || --this.count[COUNTER_POS] ==0) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
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
