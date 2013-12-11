package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.DisruptorBasedRelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinIndexRequestProcessorFactory implements IndexRequestProcessorFactory{

    private final IndexingRequestConverterFactory requestBytesConverter;
    private final RelatedProductIndexingMessageFactory indexingMessageFactory;

    private final RelatedProductIndexingMessageEventHandler roundRobinIndexingEventHandler;
    private final RelatedProductIndexingMessageEventHandler singleIndexingEventHandler;

    public RoundRobinIndexRequestProcessorFactory(IndexingRequestConverterFactory requestBytesConverter,
                                                  RelatedProductIndexingMessageFactory indexingMessageFactory,
                                                  RelatedProductIndexingMessageEventHandler roundRobinIndexingEventHandler,
                                                  RelatedProductIndexingMessageEventHandler singleIndexingEventHandler) {
        this.requestBytesConverter = requestBytesConverter;
        this.indexingMessageFactory = indexingMessageFactory;
        this.roundRobinIndexingEventHandler = roundRobinIndexingEventHandler;
        this.singleIndexingEventHandler = singleIndexingEventHandler;
    }

    @Override
    public RelatedProductIndexRequestProcessor createProcessor(Configuration configuration) {
        int requestedNumberOfRequestProcessors = configuration.getNumberOfIndexingRequestProcessors();

        if(requestedNumberOfRequestProcessors>1) {
            // needs to be passed in.
            return new DisruptorBasedRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageFactory,roundRobinIndexingEventHandler);
        }
        else {
            return new DisruptorBasedRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageFactory,singleIndexingEventHandler);

        }
    }
}
