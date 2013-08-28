package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReferenceMessageFactory;
import org.greencheek.relatedproduct.domain.RelatedProduct;
import org.greencheek.relatedproduct.domain.RelatedProductReference;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepositoryFactory;
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
class RelatedProductRoundRobinIndexRequestHandlerL1 {
    public int p01, p02, p03, p04, p05, p06, p07, p08;
    public int p11, p12, p13, p14, p15, p16, p17, p18;
}



class RelatedProductRoundRobinIndexRequestHandlerL2 extends
        RelatedProductRoundRobinIndexRequestHandlerL1
{

    protected static final Logger log = LoggerFactory.getLogger(RelatedProductRoundRobinIndexRequestHandler.class);
    protected final Disruptor<RelatedProductReference> disruptors[];
    protected final ExecutorService executors[];
    protected final int mask;

    protected final List<RelatedProduct> batchMessages;
    protected final int batchSize;
    protected final Configuration configuration;
    protected final RelatedProductIndexingMessageConverter converter;

    // Hot field, protect via class hierachy for false sharing
    protected int nextDisruptor = 0;

    protected RelatedProductRoundRobinIndexRequestHandlerL2(final Configuration configuration,
                                                       RelatedProductIndexingMessageConverter converter,
                                                       RelatedProductReferenceMessageFactory messageFactory,
                                                       RelatedProductStorageRepositoryFactory repositoryFactory,
                                                       RelatedProductStorageLocationMapper locationMapper
    ) {
        this.configuration = configuration;
        this.converter = converter;
        int numberOfIndexingRequestProcessors = Util.ceilingNextPowerOfTwo(configuration.getNumberOfIndexingRequestProcessors());
        disruptors = new Disruptor[numberOfIndexingRequestProcessors];
        executors = new ExecutorService[numberOfIndexingRequestProcessors];
        mask = numberOfIndexingRequestProcessors-1;

        int i = numberOfIndexingRequestProcessors;
        while(i--!=0) {
            ExecutorService executorService = newSingleThreadExecutor();
            executors[i]  = executorService;
            Disruptor<RelatedProductReference> disruptor = new Disruptor<RelatedProductReference>(
                    messageFactory,
                    configuration.getSizeOfIndexRequestQueue(), executorService,
                    ProducerType.SINGLE, configuration.getWaitStrategyFactory().createWaitStrategy());

            disruptors[i]  = disruptor;
            disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
            disruptor.handleEventsWith(new RingBufferRelatedProductReferenceRequestHandler(configuration.getIndexBatchSize(),repositoryFactory.getRepository(configuration),locationMapper));

            disruptor.start();

        }

        batchSize = 50;
        batchMessages= new ArrayList<RelatedProduct>(batchSize);
    }

}

class RelatedProductRoundRobinIndexRequestHandlerL3 extends RelatedProductRoundRobinIndexRequestHandlerL2 {
    public int p01, p02, p03, p04, p05, p06, p07, p08;
    public int p11, p12, p13, p14, p15, p16, p17, p18;

    protected RelatedProductRoundRobinIndexRequestHandlerL3(final Configuration configuration,
                                                  RelatedProductIndexingMessageConverter converter,
                                                  RelatedProductReferenceMessageFactory messageFactory,
                                                  RelatedProductStorageRepositoryFactory repositoryFactory,
                                                  RelatedProductStorageLocationMapper locationMapper)
    {
        super(configuration,converter,messageFactory,
        repositoryFactory,locationMapper);
    }
}

public class RelatedProductRoundRobinIndexRequestHandler extends RelatedProductRoundRobinIndexRequestHandlerL3
    implements EventHandler<RelatedProductIndexingMessage>
{


    public RelatedProductRoundRobinIndexRequestHandler(final Configuration configuration,
                                                       RelatedProductIndexingMessageConverter converter,
                                                       RelatedProductReferenceMessageFactory messageFactory,
                                                       RelatedProductStorageRepositoryFactory repositoryFactory,
                                                       RelatedProductStorageLocationMapper locationMapper
    ) {
        super(configuration,converter,messageFactory,
                repositoryFactory,locationMapper);
    }


    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {

        try {
            if(request.isValidMessage()) {
                if(request.relatedProducts.getNumberOfRelatedProducts()==0) {
                    log.info("indexing message not valid.  ignoring.  No related products");
                    return;
                }

                RelatedProduct[] products = converter.convertFrom(request);
                if(products.length==0) return;

                for(RelatedProduct p : products) batchMessages.add(p);

                if(batchMessages.size()>=batchSize || endOfBatch) {
                    log.debug("handing off request to indexing processor");
                    try {
                        disruptors[nextDisruptor++ & mask].publishEvents(new BatchCopyingRelatedProductIndexMessageTranslator(),batchMessages.toArray(new RelatedProduct[batchMessages.size()]));
                    } finally {
                        batchMessages.clear();
                    }
                }
            } else {
                log.info("indexing message not valid.  ignoring. potential related products: {}", request.relatedProducts.numberOfRelatedProducts);
            }
        } finally {
            request.setValidMessage(false);
        }
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
