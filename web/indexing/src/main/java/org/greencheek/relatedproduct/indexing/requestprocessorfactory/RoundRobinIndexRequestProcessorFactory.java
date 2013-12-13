package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.DisruptorBasedRelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * chooses between the backend processing that is done to turn the request data into a
 * {@link org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage}
 * that is stored in the backend.
 *
 * The processing is in either one of two ways:
 *
 * <pre>
 * 1)  ---->  Request --->  Ring buffer (to IndexingMessage)  --->  Storage repository
 *
 * OR
 *
 * 2)  ---->  Request --->  Ring buffer (to IndexingMessage)  --->  Ring Buffer (Reference) ---> Storage Repo
 *                                                            --->  Ring Buffer (Reference) ---> Storage Repo
 *                                                            --->  Ring Buffer (Reference) ---> Storage Repo
 * </pre>
 *
 * The choice between the two request processor is done based upon the value set for {@link org.greencheek.relatedproduct.util.config.Configuration#getNumberOfIndexingRequestProcessors()}
 */
public class RoundRobinIndexRequestProcessorFactory implements IndexRequestProcessorFactory {
    private static final Logger log = LoggerFactory.getLogger(RoundRobinIndexRequestProcessorFactory.class);


    private final IndexingRequestConverterFactory requestBytesConverter;
    private final  EventFactory<RelatedProductIndexingMessage> indexingMessageFactory;

    private final RelatedProductIndexingMessageEventHandler roundRobinIndexingEventHandler;
    private final RelatedProductIndexingMessageEventHandler singleIndexingEventHandler;

    public RoundRobinIndexRequestProcessorFactory(IndexingRequestConverterFactory requestBytesConverter,
                                                  EventFactory<RelatedProductIndexingMessage> indexingMessageFactory,
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
            try {
                // shutdown the redundant handler
                singleIndexingEventHandler.shutdown();
            } catch(Exception e) {

            }
            return new DisruptorBasedRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageFactory,roundRobinIndexingEventHandler);
        }
        else {
            try {
                // shutdown the redundant handler
                roundRobinIndexingEventHandler.shutdown();
            } catch(Exception e) {

            }

            return new DisruptorBasedRelatedProductIndexRequestProcessor(configuration,
                    requestBytesConverter,indexingMessageFactory,singleIndexingEventHandler);
        }
    }

    @Override
    public void shutdown() {
        try {
            roundRobinIndexingEventHandler.shutdown();
        } catch(Exception e) {
            log.error("Exception shutting down round robin event handler: {}", e.getMessage());
        }

        try {
            singleIndexingEventHandler.shutdown();
        } catch(Exception e) {
            log.error("Exception shutting down single event handler: {}", e.getMessage());
        }
    }
}
