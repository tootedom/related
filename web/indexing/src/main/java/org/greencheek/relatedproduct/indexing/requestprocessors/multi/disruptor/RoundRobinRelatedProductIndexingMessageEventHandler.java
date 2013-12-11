package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReferenceMessageFactory;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.domain.RelatedProductReference;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class RoundRobinRelatedProductIndexingMessageEventHandler implements RelatedProductIndexingMessageEventHandler
{

    protected static final Logger log = LoggerFactory.getLogger(RoundRobinRelatedProductIndexingMessageEventHandler.class);
    protected final Disruptor<RelatedProductReference> disruptors[];
    protected final ExecutorService executors[];
    protected final RelatedProductReferenceEventHandler handlers[];
    protected final int mask;

    protected final List<RelatedProduct> batchMessages;

    protected final int batchSize;
    protected final Configuration configuration;
    protected final RelatedProductIndexingMessageConverter converter;

    private final int[] nextDisruptor = new int[30];


    public RoundRobinRelatedProductIndexingMessageEventHandler(final Configuration configuration,
                                                               RelatedProductIndexingMessageConverter converter,
                                                               RelatedProductReferenceMessageFactory messageFactory,
                                                               RelatedProductReferenceEventHandlerFactory relatedProductIndexingEventHandlerFactory
    ) {
        this.configuration = configuration;
        this.converter = converter;
        int numberOfIndexingRequestProcessors = Util.ceilingNextPowerOfTwo(configuration.getNumberOfIndexingRequestProcessors());
        disruptors = new Disruptor[numberOfIndexingRequestProcessors];
        executors = new ExecutorService[numberOfIndexingRequestProcessors];
        handlers = new RelatedProductReferenceEventHandler[numberOfIndexingRequestProcessors];

        mask = numberOfIndexingRequestProcessors-1;
        final int sizeOfQueue;
        if(configuration.getSizeOfBatchIndexingRequestQueue()==-1) {
            sizeOfQueue = Util.ceilingNextPowerOfTwo(configuration.getSizeOfIncomingMessageQueue()/numberOfIndexingRequestProcessors);
        } else {
            sizeOfQueue = Util.ceilingNextPowerOfTwo(configuration.getSizeOfBatchIndexingRequestQueue());
        }
        int i = numberOfIndexingRequestProcessors;
        while(i--!=0) {
            ExecutorService executorService = newSingleThreadExecutor();
            executors[i]  = executorService;
            Disruptor<RelatedProductReference> disruptor = new Disruptor<RelatedProductReference>(
                    messageFactory,
                    sizeOfQueue, executorService,
                    ProducerType.SINGLE, configuration.getWaitStrategyFactory().createWaitStrategy());

            disruptors[i]  = disruptor;
            handlers[i] = relatedProductIndexingEventHandlerFactory.getHandler();
            disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
            disruptor.handleEventsWith(handlers[i]);

            disruptor.start();

        }

        batchSize = configuration.getIndexBatchSize();

        batchMessages= new ArrayList<RelatedProduct>(batchSize + configuration.getMaxNumberOfRelatedProductsPerPurchase());
    }

    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {

        try {
            if(request.isValidMessage()) {
                if(request.relatedProducts.getNumberOfRelatedProducts()==0) {
                    log.info("indexing message not valid.  ignoring.  No related products");
                    return;
                }

                for(RelatedProduct p : converter.convertFrom(request)) batchMessages.add(p);

                if(endOfBatch || batchMessages.size()>=batchSize) {
                    log.debug("handing off request to indexing processor");
                    try {
                        disruptors[nextDisruptor[7]++ & mask].publishEvents(BatchCopyingRelatedProductIndexMessageTranslator.INSTANCE,batchMessages.toArray(new RelatedProduct[batchMessages.size()]));
                    } finally {
                        batchMessages.clear();
                    }
                }
            } else {
                log.info("indexing message not valid, and will be ignored.  Potentially contained {} related products", request.getRelatedProducts().getNumberOfRelatedProducts());
            }
        } finally {
            request.setValidMessage(false);
        }
    }


    public void shutdown() {
        for(ExecutorService executorService : executors) {
            try {
                executorService.shutdownNow();
            } catch(Exception e) {
                log.error("Problem during shutdown terminating the executorservice",e);
            }
        }

        for(Disruptor disruptor : disruptors) {
            try {
                disruptor.shutdown();
            } catch(Exception e) {
                log.error("Problem during shutdown of the disruptor",e);
            }
        }

        for(RelatedProductReferenceEventHandler handler : handlers) {
            try {
                handler.shutdown();
            } catch(Exception e) {
                log.error("Issue terminating handler",e);
            }
        }
    }

}

