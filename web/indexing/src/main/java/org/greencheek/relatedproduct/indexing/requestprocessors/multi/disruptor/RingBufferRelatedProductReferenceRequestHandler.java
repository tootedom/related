package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.domain.RelatedProduct;


import org.greencheek.relatedproduct.domain.RelatedProductReference;
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
class RingBufferRelatedProductReferencRequestHandlerL1 {
    private int p01, p02, p03, p04, p05, p06, p07, p08;
    private int p11, p12, p13, p14, p15, p16, p17, p18;
}

class RingBufferRelatedProductReferencRequestHandlerL2 extends RingBufferRelatedProductReferencRequestHandlerL1 {

    protected static final Logger log = LoggerFactory.getLogger(RingBufferRelatedProductReferenceRequestHandler.class);

    protected final RelatedProductStorageRepository storageRepository;

    protected final List<RelatedProduct> relatedProducts;

    protected final RelatedProductStorageLocationMapper locationMapper;

    protected final int batchSize;

    protected int count;

    protected RingBufferRelatedProductReferencRequestHandlerL2(int batchSize,
                                         RelatedProductStorageRepository repository,
                                         RelatedProductStorageLocationMapper locationMapper) {

        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = batchSize;
        this.count = batchSize;
        this.relatedProducts = new ArrayList<RelatedProduct>(batchSize);
    }

}

class RingBufferRelatedProductReferencRequestHandlerL3 extends  RingBufferRelatedProductReferencRequestHandlerL2 {
    private int p01, p02, p03, p04, p05, p06, p07, p08;
    private int p11, p12, p13, p14, p15, p16, p17, p18;

    protected RingBufferRelatedProductReferencRequestHandlerL3(int batchSize,
                                           RelatedProductStorageRepository repository,
                                           RelatedProductStorageLocationMapper locationMapper) {
        super(batchSize,repository,locationMapper);
    }
}



public class RingBufferRelatedProductReferenceRequestHandler extends RingBufferRelatedProductReferencRequestHandlerL3 implements EventHandler<RelatedProductReference> {

    public RingBufferRelatedProductReferenceRequestHandler(int batchSize,
                                                           RelatedProductStorageRepository repository,
                                                           RelatedProductStorageLocationMapper locationMapper)
    {
        super(batchSize,repository,locationMapper);
    }

    @Override
    public void onEvent(RelatedProductReference request, long l, boolean endOfBatch) throws Exception {

        try {
            --count;

            relatedProducts.add(request.getReference());

            if(endOfBatch || count==0) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
                    try {
                        storageRepository.store(locationMapper,relatedProducts.toArray(new RelatedProduct[relatedProducts.size()]));
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}",relatedProducts,e);
                    }
                }
                finally {
                    count = batchSize;
                    relatedProducts.clear();
                }
            }
        } finally {
          request.setReference(null);
        }


    }

}
