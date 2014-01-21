package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import org.greencheek.related.indexing.RelatedItemStorageLocationMapper;
import org.greencheek.related.indexing.RelatedItemStorageRepositoryFactory;
import org.greencheek.related.util.config.Configuration;

/**
 * For each call of the {@link #getHandler()} method creates a new {@link BatchingRelatedItemReferenceEventHandler}
 */
public class BatchingRelatedItemReferenceEventHanderFactory implements RelatedItemReferenceEventHandlerFactory {

    private final Configuration configuration;
    private final RelatedItemStorageLocationMapper locationMapper;
    private final RelatedItemStorageRepositoryFactory repository;

    public BatchingRelatedItemReferenceEventHanderFactory(Configuration config,
                                                          RelatedItemStorageRepositoryFactory repository,
                                                          RelatedItemStorageLocationMapper locationMapper) {
        this.configuration = config;
        this.repository = repository;
        this.locationMapper = locationMapper;
    }


    @Override
    public RelatedItemReferenceEventHandler getHandler() {
        return new BatchingRelatedItemReferenceEventHandler(configuration.getIndexBatchSize(),repository.getRepository(configuration),locationMapper);
    }
}
