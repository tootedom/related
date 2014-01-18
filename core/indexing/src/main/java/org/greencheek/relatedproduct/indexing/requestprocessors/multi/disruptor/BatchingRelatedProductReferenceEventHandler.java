package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import org.greencheek.relatedproduct.api.indexing.RelatedProduct;


import org.greencheek.relatedproduct.api.indexing.RelatedProductReference;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

/**
 *
 */
public class BatchingRelatedProductReferenceEventHandler implements RelatedProductReferenceEventHandler {

    protected static final Logger log = LoggerFactory.getLogger(BatchingRelatedProductReferenceEventHandler.class);

    protected final RelatedProductStorageRepository storageRepository;

    protected final List<RelatedProduct> relatedProducts;

    protected final RelatedProductStorageLocationMapper locationMapper;

    protected final int batchSize;

    private final int[] count = new int[30];
    private static final int COUNTER_POS = 14;

    public BatchingRelatedProductReferenceEventHandler(int batchSize,
                                                       RelatedProductStorageRepository repository,
                                                       RelatedProductStorageLocationMapper locationMapper)
    {
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = batchSize;
        this.count[COUNTER_POS] = batchSize;
        this.relatedProducts = new ArrayList<RelatedProduct>(batchSize);
    }

    @Override
    public void onEvent(RelatedProductReference request, long l, boolean endOfBatch) throws Exception {

        try {
            relatedProducts.add(request.getReference());

            if(endOfBatch || --this.count[COUNTER_POS] ==0) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
                    try {
                        storageRepository.store(locationMapper,relatedProducts);
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}",relatedProducts,e);
                    }
                }
                finally {
                    this.count[COUNTER_POS]  = batchSize;
                    relatedProducts.clear();
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
