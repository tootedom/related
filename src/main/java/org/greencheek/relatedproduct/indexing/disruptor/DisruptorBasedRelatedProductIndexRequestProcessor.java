package org.greencheek.relatedproduct.indexing.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.indexing.InvalidRelatedProductJsonException;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


@Named(value="indexrequestprocessor")
@Singleton
public class DisruptorBasedRelatedProductIndexRequestProcessor implements RelatedProductIndexRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductIndexRequestProcessor.class);

    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductIndexingMessage> disruptor;

    private final Configuration configuration;

    @Inject
    public DisruptorBasedRelatedProductIndexRequestProcessor(EventHandler<RelatedProductIndexingMessage> eventHandler,
                                                             Configuration configuration) {
        this.configuration = configuration;
        disruptor = new Disruptor<RelatedProductIndexingMessage>(
                RelatedProductIndexingMessage.FACTORY,
                configuration.getSizeOfIndexRequestQueue(), executorService,
                ProducerType.SINGLE, new SleepingWaitStrategy());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }

    @Override
    public void processRequest(byte[] data) {
        if(data.length==0) {
            log.warn("No data to index. Ignoring");
            return;
        }

        try {
            RelatedProductPurchaseFromJson translator = new RelatedProductPurchaseFromJson(data,configuration);
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

    }


}
