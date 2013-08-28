package org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.domain.RelatedProduct;


import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 28/04/2013
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
class RingBufferRelatedProductIndexingRequestHandlerL1 {
    private int p01, p02, p03, p04, p05, p06, p07, p08;
    private int p11, p12, p13, p14, p15, p16, p17, p18;
}

class RingBufferRelatedProductIndexingRequestHandlerL2 extends RingBufferRelatedProductIndexingRequestHandlerL1 {

    protected static final Logger log = LoggerFactory.getLogger(RingBufferRelatedProductIndexingRequestHandler.class);

    protected final RelatedProductIndexingMessageConverter indexConverter;
    protected final RelatedProductStorageRepository storageRepository;

    protected final List<RelatedProduct> relatedProducts = new ArrayList<RelatedProduct>(1024);

    protected final RelatedProductStorageLocationMapper locationMapper;

    protected final int batchSize;

    protected int count;

    protected RingBufferRelatedProductIndexingRequestHandlerL2(int batchSize,
                                         RelatedProductIndexingMessageConverter converter,
                                         RelatedProductStorageRepository repository,
                                         RelatedProductStorageLocationMapper locationMapper) {

        this.indexConverter = converter;
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = batchSize;
        this.count = batchSize;
    }

}

class RingBufferRelatedProductIndexingRequestHandlerL3 extends  RingBufferRelatedProductIndexingRequestHandlerL2 {
    private int p01, p02, p03, p04, p05, p06, p07, p08;
    private int p11, p12, p13, p14, p15, p16, p17, p18;

    protected RingBufferRelatedProductIndexingRequestHandlerL3(int batchSize,
                                           RelatedProductIndexingMessageConverter converter,
                                           RelatedProductStorageRepository repository,
                                           RelatedProductStorageLocationMapper locationMapper) {
        super(batchSize,converter,repository,locationMapper);
    }
}



public class RingBufferRelatedProductIndexingRequestHandler extends RingBufferRelatedProductIndexingRequestHandlerL3 implements EventHandler<RelatedProductIndexingMessage> {

    public RingBufferRelatedProductIndexingRequestHandler(int batchSize,
                                                          RelatedProductIndexingMessageConverter converter,
                                                          RelatedProductStorageRepository repository,
                                                          RelatedProductStorageLocationMapper locationMapper)
    {
        super(batchSize,converter,repository,locationMapper);
    }

    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {


        if(!request.isValidMessage()) {
            log.debug("Invalid indexing message.  Ignoring message");
            return;
        }
        if(request.relatedProducts.getNumberOfRelatedProducts()==0) {
            log.debug("Invalid indexing message, no related products.  Ignoring message");
            request.setValidMessage(false);
            return;
        }

        try {

            RelatedProduct[] products = indexConverter.convertFrom(request);
            count-=products.length;
            for(RelatedProduct p : products) {
                relatedProducts.add(p);
            }

            if(endOfBatch || count<=0) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
                    try {
                        storageRepository.store(locationMapper,relatedProducts.toArray(new RelatedProduct[relatedProducts.size()]));
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}",products,e);
                    }
                }
                finally {
                    count = batchSize;
                    relatedProducts.clear();
                }
            }
        } finally {
            request.setValidMessage(false);
        }


    }

}
