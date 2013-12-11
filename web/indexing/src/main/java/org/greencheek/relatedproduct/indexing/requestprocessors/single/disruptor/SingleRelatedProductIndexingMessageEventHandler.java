package org.greencheek.relatedproduct.indexing.requestprocessors.single.disruptor;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessageConverter;
import org.greencheek.relatedproduct.domain.RelatedProduct;


import org.greencheek.relatedproduct.indexing.RelatedProductStorageLocationMapper;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;
import org.greencheek.relatedproduct.indexing.requestprocessors.RelatedProductIndexingMessageEventHandler;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;


public class SingleRelatedProductIndexingMessageEventHandler implements RelatedProductIndexingMessageEventHandler {

    protected static final Logger log = LoggerFactory.getLogger(SingleRelatedProductIndexingMessageEventHandler.class);

    protected final RelatedProductIndexingMessageConverter indexConverter;
    protected final RelatedProductStorageRepository storageRepository;

    protected final List<RelatedProduct> relatedProducts;

    protected final RelatedProductStorageLocationMapper locationMapper;

    protected final int batchSize;

    protected int count;

    public SingleRelatedProductIndexingMessageEventHandler(Configuration configuration,
                                                           RelatedProductIndexingMessageConverter converter,
                                                           RelatedProductStorageRepository repository,
                                                           RelatedProductStorageLocationMapper locationMapper)
    {
        this.indexConverter = converter;
        this.storageRepository = repository;
        this.locationMapper = locationMapper;
        this.batchSize = configuration.getIndexBatchSize();
        this.count = batchSize;
        this.relatedProducts = new ArrayList<RelatedProduct>(batchSize + configuration.getMaxNumberOfRelatedProductsPerPurchase());
    }

    @Override
    public void onEvent(RelatedProductIndexingMessage request, long l, boolean endOfBatch) throws Exception {


        if(!request.isValidMessage()) {
            log.debug("Invalid indexing message.  Ignoring message");
            return;
        }
        if(request.getRelatedProducts().getNumberOfRelatedProducts()==0) {
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

            if(endOfBatch || count<1) {
                try {
                    log.debug("Sending indexing requests to the storage repository");
                    try {
                        storageRepository.store(locationMapper,relatedProducts);
                    } catch(Exception e) {
                        log.warn("Exception calling storage repository for related products:{}", products, e);
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

    @Override
    public void shutdown() {
        try {
            storageRepository.shutdown();
        } catch(Exception e) {
            log.warn("Unable to shutdown respository client");
        }
    }
}
