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
    public int p01, p02, p03, p04, p05, p06, p07, p08;
    public int p11, p12, p13, p14, p15, p16, p17, p18;
    public int p21, p22, p23, p24, p25, p26, p27, p28;
    public int p31, p32, p33, p34, p35, p36, p37, p38;
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
    public int e01, e02, e03, e04, e05, e06, e07, e08;
    public int e11, e12, e13, e14, e15, e16, e17, e18;
    public int e21, e22, e23, e24, e25, e26, e27, e28;
    public int e31, e32, e33, e34, e35, e36, e37, e38;

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
                        storageRepository.store(locationMapper,relatedProducts);
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
