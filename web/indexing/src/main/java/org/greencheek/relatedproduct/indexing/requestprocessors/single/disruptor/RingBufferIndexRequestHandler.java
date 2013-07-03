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
public class RingBufferIndexRequestHandler implements EventHandler<RelatedProductIndexingMessage> {

    private static final Logger log = LoggerFactory.getLogger(RingBufferIndexRequestHandler.class);

    private final RelatedProductIndexingMessageConverter indexConverter;
    private final RelatedProductStorageRepository storageRepository;

    private final List<RelatedProduct> relatedProducts = new ArrayList<RelatedProduct>(1024);

    private final RelatedProductStorageLocationMapper locationMapper;

    private final int batchSize;

    private int count;

    public RingBufferIndexRequestHandler(int batchSize,
                                         RelatedProductIndexingMessageConverter converter,
                                         RelatedProductStorageRepository repository,
                                         RelatedProductStorageLocationMapper locationMapper) {

        this.indexConverter = converter;
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = batchSize;
        this.count = batchSize;
    }



    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {


        if(!request.validMessage.get()) {
            log.debug("Invalid indexing message.  Ignoring message");
            return;
        }
        if(request.relatedProducts.numberOfRelatedProducts.get()==0) {
            log.debug("Invalid indexing message, no related products.  Ignoring message");
            request.validMessage.set(false);
            return;
        }

        try {

            Set<RelatedProduct> products = indexConverter.convertFrom(request);
            count-=products.size();
            relatedProducts.addAll(products);

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
                    relatedProducts.clear();
                }
            }
        } finally {
            request.validMessage.set(false);
        }


    }

}
