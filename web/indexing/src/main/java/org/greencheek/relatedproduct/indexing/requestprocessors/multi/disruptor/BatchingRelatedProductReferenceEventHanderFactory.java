package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * For each call of the {@link #getHandler()} method creates a new {@link BatchingRelatedProductReferenceEventHandler}
 */
public class BatchingRelatedProductReferenceEventHanderFactory implements RelatedProductReferenceEventHandlerFactory {

    private final Configuration configuration;
    private final RelatedProductStorageLocationMapper locationMapper;
    private final RelatedProductStorageRepositoryFactory repository;

    public BatchingRelatedProductReferenceEventHanderFactory(Configuration config,
                                                             RelatedProductStorageRepositoryFactory repository,
                                                             RelatedProductStorageLocationMapper locationMapper) {
        this.configuration = config;
        this.repository = repository;
        this.locationMapper = locationMapper;
    }


    @Override
    public RelatedProductReferenceEventHandler getHandler() {
        return new BatchingRelatedProductReferenceEventHandler(configuration.getIndexBatchSize(),repository.getRepository(configuration),locationMapper);
    }
}
