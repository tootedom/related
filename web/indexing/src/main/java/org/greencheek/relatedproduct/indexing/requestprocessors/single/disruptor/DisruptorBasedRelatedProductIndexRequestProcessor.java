package org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.*;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class DisruptorBasedRelatedProductIndexRequestProcessor implements RelatedProductIndexRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductIndexRequestProcessor.class);

    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductIndexingMessage> disruptor;

    private final IndexingRequestConverterFactory requestConverter;

    private final RelatedProductStorageRepository storageRepository;

    public DisruptorBasedRelatedProductIndexRequestProcessor(Configuration configuration,
                                                             IndexingRequestConverterFactory requestConverter,
                                                             RelatedProductIndexingMessageConverter indexingMessageToRelatedProductsConvertor,
                                                             RelatedProductIndexingMessageFactory messageFactory,
                                                             RelatedProductStorageRepositoryFactory repositoryFactory,
                                                             RelatedProductStorageLocationMapper locationMapper) {
        this.requestConverter = requestConverter;
        this.storageRepository = repositoryFactory.getRepository(configuration);
        disruptor = new Disruptor<RelatedProductIndexingMessage>(
                messageFactory,
                Util.ceilingNextPowerOfTwo(configuration.getSizeOfIncomingMessageQueue()), executorService,
                ProducerType.SINGLE, configuration.getWaitStrategyFactory().createWaitStrategy());

        final int batchIndexSize = configuration.getIndexBatchSize();
        disruptor.handleEventsWith(new EventHandler[] {new SingleRelatedProductIndexingMessageEventHandler(batchIndexSize,indexingMessageToRelatedProductsConvertor,storageRepository,locationMapper)});
        disruptor.start();

    }

    @Override
    public void processRequest(Configuration config, ByteBuffer data) {
        int size = data.remaining();
        try {
            IndexingRequestConverter converter = requestConverter.createConverter(config,data);
            disruptor.publishEvent(converter);
        } catch(InvalidRelatedProductJsonException e) {
            log.warn("Invalid json content, unable to process request.  Length of data:{}", size);

            if(log.isDebugEnabled()) {
                if(data.hasArray()) {
                    log.debug("Invalid content as byte array: {}", Arrays.toString(data.array()));
                }
            }
        }
    }


    public void shutdown() {

        try {
            storageRepository.shutdown();
        } catch(Exception e ) {
            log.warn("Unable to shutdown respository client");
        }

        try {
            log.info("Attempting to shut down executor thread pool in index request processor");
            executorService.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in index request processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in index request processor");
            disruptor.halt();
            disruptor.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in index request processor",e);
        }

    }


}
