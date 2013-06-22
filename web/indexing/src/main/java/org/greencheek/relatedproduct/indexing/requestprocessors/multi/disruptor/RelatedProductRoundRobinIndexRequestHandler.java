package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageFactory;
import org.greencheek.relatedproduct.searching.domain.RelatedProduct;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
import org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor.RingBufferIndexRequestHandler;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 28/04/2013
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductRoundRobinIndexRequestHandler implements EventHandler<RelatedProductIndexingMessage> {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductRoundRobinIndexRequestHandler.class);

    private final Disruptor<RelatedProductIndexingMessage> disruptors[];
    private final ExecutorService executors[];

    private int nextDisruptor = 0;

    private final int mask;

    public RelatedProductRoundRobinIndexRequestHandler(Configuration configuration,
                                                       RelatedProductIndexingMessageConverter converter,
                                                       RelatedProductIndexingMessageFactory messageFactory,
                                                       RelatedProductStorageRepositoryFactory repositoryFactory,
                                                       RelatedProductStorageLocationMapper locationMapper
    ) {

        int numberOfIndexingRequestProcessors = Util.ceilingNextPowerOfTwo(configuration.getNumberOfIndexingRequestProcessors());
        disruptors = new Disruptor[numberOfIndexingRequestProcessors];
        executors = new ExecutorService[numberOfIndexingRequestProcessors];
        mask = numberOfIndexingRequestProcessors-1;

        int i = numberOfIndexingRequestProcessors;
        while(i--!=0) {
            ExecutorService executorService = newSingleThreadExecutor();
            executors[i]  = executorService;
            Disruptor<RelatedProductIndexingMessage> disruptor = new Disruptor<RelatedProductIndexingMessage>(
                messageFactory,
                configuration.getSizeOfIndexRequestQueue(), executorService,
                ProducerType.SINGLE, new SleepingWaitStrategy());

            disruptors[i]  = disruptor;
            disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
            disruptor.handleEventsWith(new RingBufferIndexRequestHandler(converter,repositoryFactory.getRepository(configuration),locationMapper));
            disruptor.start();

        }

    }


    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {
        log.debug("handing off request to indexing processor");
        disruptors[nextDisruptor++ & mask].publishEvent(new CopyingRelatedProductIndexMessageTranslator(request));
    }


    public void shutdown() {
        for(ExecutorService executorService : executors) {
            executorService.shutdownNow();
        }

        for(Disruptor disruptor : disruptors) {
            disruptor.shutdown();
        }
    }

}
