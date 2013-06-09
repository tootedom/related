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

//        if(request.getProducts().size()==0) return;

        if(!request.validMessage.get()) return;
        if(request.relatedProducts.numberOfRelatedProducts.get()==0) return;

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

//
//        List<RelatedProductInfo> purchases = convertToRelatedPurchaseItems(request);

//        for(RelatedProductInfo purchase : purchases) {
//            XContentBuilder builder =  jsonBuilder().startObject()
//                    .field("pid", purchase.getPid())
//                    .field("date",purchase.getDate() )
//                    .field("channel", purchase.getChannel())
//                    .field("site", purchase.getSite())
//                    .array("bought-with",purchase.getRelatedPids().toArray())
//                    .endObject();
//
//            String doc = builder.string();
//            Index index = new Index.Builder(doc).index("relatedpurchases-"+now()).type("relatedproduct").build();
//            try {
//                restClient.execute(index);
//            } catch (Exception e) {
//                log.warn("Exception indexing content : {}",doc);
//            }
//
//        }

    }

//    private List<RelatedProductInfo> convertToRelatedPurchaseItems(IndexRequest request) {
//        List<IndexRequest.Product> products = request.getProducts();
//        int size = products.size();
//        List<RelatedProductInfo> purchaseRequests = new ArrayList<RelatedProductInfo>(size);
//
//        List<IndexRequest.Product> ids = new ArrayList<IndexRequest.Product>(size-1);
//
//        while(size--!=0) {
//            IndexRequest.Product product = products.get(size);
//            Set<IndexRequest.Product> otherItems = new HashSet<IndexRequest.Product>(products);
//            otherItems.remove(product);
//
//            purchaseRequests.add(createRelatedPurchase(request,product,otherItems));
//        }
//
//        return purchaseRequests;
//    }
//
//    private RelatedProductInfo createRelatedPurchase(IndexRequest request, IndexRequest.Product mainProduct, Set<IndexRequest.Product> boughtWith) {
//        String[] pids = new String[boughtWith.size()];
//        int i = 0;
//        for(IndexRequest.Product p : boughtWith) {
//            pids[i++] = p.getPid();
//        }
//
////        return new RelatedProductInfo(mainProduct.getPid(),request.getDate(),request.getChannel(),request.getSite(),pids);
//        return null;
//    }

}
