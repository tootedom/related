package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor.DisruptorBasedRoundRobinRelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor.DisruptorBasedRelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@Named
public class RoundRobinIndexRequestProcessorFactory implements IndexRequestProcessorFactory{

    private final IndexingRequestConverterFactory requestBytesConverter;
    private final RelatedProductIndexingMessageFactory indexingMessageFactory;
    private final RelatedProductIndexingMessageConverter indexingMessageToRelatedProductsConverter;
    private final RelatedProductStorageRepositoryFactory repoFactory;
    private final RelatedProductStorageLocationMapper locationMapper;

    @Inject
    public RoundRobinIndexRequestProcessorFactory(IndexingRequestConverterFactory requestBytesConverter,
                                                  RelatedProductIndexingMessageFactory indexingMessageFactory,
                                                  RelatedProductIndexingMessageConverter indexingMessageToRelatedProductsConverter,
                                                  RelatedProductStorageRepositoryFactory repoFactory,
                                                  RelatedProductStorageLocationMapper locationMapper) {
        this.requestBytesConverter = requestBytesConverter;
        this.indexingMessageFactory = indexingMessageFactory;
        this.indexingMessageToRelatedProductsConverter = indexingMessageToRelatedProductsConverter;
        this.repoFactory = repoFactory;
        this.locationMapper = locationMapper;
    }

    @Override
    public RelatedProductIndexRequestProcessor createProcessor(Configuration configuration) {
        short requestedNumberOfRequestProcessors = configuration.getNumberOfIndexingRequestProcessors();

        if(requestedNumberOfRequestProcessors>1) {
            return new DisruptorBasedRoundRobinRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageToRelatedProductsConverter,indexingMessageFactory,repoFactory,locationMapper);
        }
        else {
            return new DisruptorBasedRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageToRelatedProductsConverter,indexingMessageFactory,repoFactory,locationMapper);
        }
    }
}
