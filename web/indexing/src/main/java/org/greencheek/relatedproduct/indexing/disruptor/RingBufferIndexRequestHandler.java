package org.greencheek.relatedproduct.indexing.disruptor;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.domain.RelatedProduct;


import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;
import javax.inject.Named;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 28/04/2013
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
@Named
public class RingBufferIndexRequestHandler implements EventHandler<RelatedProductIndexingMessage> {

    private static final Logger log = LoggerFactory.getLogger(RingBufferIndexRequestHandler.class);

    private final RelatedProductIndexingMessageConverter indexConverter;
    private final RelatedProductStorageRepository storageRepository;

    private final List<RelatedProduct> relatedProducts = new ArrayList<RelatedProduct>(1024);



    @Inject
    public RingBufferIndexRequestHandler(RelatedProductIndexingMessageConverter converter,
                                         RelatedProductStorageRepository repository) {

        this.indexConverter = converter;
        this.storageRepository = repository;
    }



    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {


        if(!request.validMessage.get()) return;
        if(request.relatedProducts.numberOfRelatedProducts.get()==0) {
            request.validMessage.set(false);
            return;
        }

        try {
            Set<RelatedProduct> products = indexConverter.convertFrom(request);
            relatedProducts.addAll(products);

            if(endOfBatch) {
                try {
                    storageRepository.store(relatedProducts.toArray(new RelatedProduct[relatedProducts.size()]));
                } catch(Exception e) {
                    log.warn("Exception calling storage repository for related products:{}",products,e);
                }

                relatedProducts.clear();
            }
        } finally {
            request.validMessage.set(false);
        }


    }

}
