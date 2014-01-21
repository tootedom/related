package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.related.api.indexing.*;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.indexing.requestprocessors.RelatedItemIndexingMessageEventHandler;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.arrayindexing.Util;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;


public class RoundRobinRelatedItemIndexingMessageEventHandler implements RelatedItemIndexingMessageEventHandler
{

    protected static final Logger log = LoggerFactory.getLogger(RoundRobinRelatedItemIndexingMessageEventHandler.class);
    protected final Disruptor<RelatedItemReference> disruptors[];
    protected final ExecutorService executors[];
    protected final RelatedItemReferenceEventHandler handlers[];
    protected final int mask;

    protected final List<RelatedItem> batchMessages;

    protected final int batchSize;
    protected final Configuration configuration;
    protected final RelatedItemIndexingMessageConverter converter;

    private final int[] nextDisruptor = new int[30];
    private static final int COUNTER_POS = 14;

    private volatile boolean shutdown = false;

    public RoundRobinRelatedItemIndexingMessageEventHandler(final Configuration configuration,
                                                            RelatedItemIndexingMessageConverter converter,
                                                            RelatedItemReferenceMessageFactory messageFactory,
                                                            RelatedItemReferenceEventHandlerFactory relatedItemIndexingEventHandlerFactory
    ) {
        this.configuration = configuration;
        this.converter = converter;
        int numberOfIndexingRequestProcessors = Util.ceilingNextPowerOfTwo(configuration.getNumberOfIndexingRequestProcessors());
        disruptors = new Disruptor[numberOfIndexingRequestProcessors];
        executors = new ExecutorService[numberOfIndexingRequestProcessors];
        handlers = new RelatedItemReferenceEventHandler[numberOfIndexingRequestProcessors];

        mask = numberOfIndexingRequestProcessors-1;
        final int sizeOfQueue;
        if(configuration.getSizeOfBatchIndexingRequestQueue()==-1) {
            sizeOfQueue = Util.ceilingNextPowerOfTwo(configuration.getSizeOfIncomingMessageQueue()/numberOfIndexingRequestProcessors);
        } else {
            sizeOfQueue = Util.ceilingNextPowerOfTwo(configuration.getSizeOfBatchIndexingRequestQueue());
        }
        int i = numberOfIndexingRequestProcessors;
        while(i--!=0) {
            ExecutorService executorService = getExecutorService();
            executors[i]  = executorService;
            Disruptor<RelatedItemReference> disruptor = new Disruptor<RelatedItemReference>(
                    messageFactory,
                    sizeOfQueue, executorService,
                    ProducerType.SINGLE, configuration.getWaitStrategyFactory().createWaitStrategy());

            disruptors[i]  = disruptor;
            handlers[i] = relatedItemIndexingEventHandlerFactory.getHandler();
            disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
            disruptor.handleEventsWith(handlers[i]);

            disruptor.start();

        }
        nextDisruptor[COUNTER_POS] = 1;
        batchSize = configuration.getIndexBatchSize();

        batchMessages= new ArrayList<RelatedItem>(batchSize + configuration.getMaxNumberOfRelatedItemsPerItem());
    }


    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("IndexingMessageEventHandler"));
    }

    @Override
    public void onEvent(RelatedItemIndexingMessage request, long l, boolean endOfBatch) throws Exception {

        try {
            if(request.isValidMessage()) {
                if(request.getRelatedItems().getNumberOfRelatedItems()==0) {
                    log.info("indexing message not valid.  ignoring.  No related products");
                    return;
                }

                for(RelatedItem p : converter.convertFrom(request)) batchMessages.add(p);

                if(endOfBatch || batchMessages.size()>=batchSize) {
                    log.debug("handing off request to indexing processor");
                    try {
                        disruptors[nextDisruptor[COUNTER_POS]++ & mask].publishEvents(BatchCopyingRelatedItemIndexMessageTranslator.INSTANCE,batchMessages.toArray(new RelatedItem[batchMessages.size()]));
                    } finally {
                        batchMessages.clear();
                    }
                }
            } else {
                log.info("indexing message not valid, and will be ignored.  Potentially contained {} related products", request.getRelatedItems().getNumberOfRelatedItems());
            }
        } finally {
            request.setValidMessage(false);
        }
    }


    public void shutdown() {
        if(!shutdown) {
            shutdown=true;
            for(RelatedItemReferenceEventHandler handler : handlers) {
                try {
                    handler.shutdown();
                } catch(Exception e) {
                    log.error("Issue terminating handler",e);
                }
            }

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
        }
    }

}

