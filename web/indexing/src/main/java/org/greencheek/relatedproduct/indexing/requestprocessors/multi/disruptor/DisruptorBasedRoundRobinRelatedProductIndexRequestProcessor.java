package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.indexing.*;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class DisruptorBasedRoundRobinRelatedProductIndexRequestProcessor implements RelatedProductIndexRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRoundRobinRelatedProductIndexRequestProcessor.class);

    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductIndexingMessage> disruptor;

    private final IndexingRequestConverterFactory requestConverter;
    private final RelatedProductRoundRobinIndexRequestHandler eventHandler;

    public DisruptorBasedRoundRobinRelatedProductIndexRequestProcessor(Configuration configuration,
                                                                       IndexingRequestConverterFactory requestConverter,
                                                                       RelatedProductIndexingMessageConverter converter,
                                                                       RelatedProductIndexingMessageFactory messageFactory,
                                                                       RelatedProductStorageRepositoryFactory factory,
                                                                       RelatedProductStorageLocationMapper locationMapper) {


        this.requestConverter = requestConverter;
        disruptor = new Disruptor<RelatedProductIndexingMessage>(
                messageFactory,
                configuration.getSizeOfIndexRequestQueue(), executorService,
                ProducerType.MULTI, new SleepingWaitStrategy());


        eventHandler = new RelatedProductRoundRobinIndexRequestHandler(configuration,converter,messageFactory,factory,locationMapper);
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    @Override
    public void processRequest(Configuration config, byte[] data) {
        if(data.length==0) {
            log.warn("No data to index. Ignoring");
            return;
        }

        try {
            IndexingRequestConverter converter = requestConverter.createConverter(config,data);
            RelatedProductInitialIndexingRequestTranslator translator = new RelatedProductInitialIndexingRequestTranslator(config,converter);
            disruptor.publishEvent(translator);
        } catch(InvalidRelatedProductJsonException e) {
            log.warn("Invalid json content, unable to process request.  Length of data:{}", data.length);

            if(log.isDebugEnabled()) {
                log.debug("Invalid content as byte array: {}", Arrays.toString(data));
            }
        }
    }


    @PreDestroy
    public void shutdown() {

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

        log.info("Shutting down round robin index request event handler");
        eventHandler.shutdown();


    }


}
