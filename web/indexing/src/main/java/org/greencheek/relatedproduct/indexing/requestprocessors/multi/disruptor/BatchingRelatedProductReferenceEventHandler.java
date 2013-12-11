package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import org.greencheek.relatedproduct.domain.RelatedProduct;


import org.greencheek.relatedproduct.domain.RelatedProductReference;
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

    protected int count;

    public BatchingRelatedProductReferenceEventHandler(int batchSize,
                                                       RelatedProductStorageRepository repository,
                                                       RelatedProductStorageLocationMapper locationMapper)
    {
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = batchSize;
        this.count = batchSize;
        this.relatedProducts = new ArrayList<RelatedProduct>(batchSize);
    }

    @Override
    public void onEvent(RelatedProductReference request, long l, boolean endOfBatch) throws Exception {

        try {

            relatedProducts.add(request.getReference());

            if(endOfBatch || --count==0) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
                    try {
                        storageRepository.store(locationMapper,relatedProducts);
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}",relatedProducts,e);
                    }
                }
                finally {
                    count = batchSize;
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
