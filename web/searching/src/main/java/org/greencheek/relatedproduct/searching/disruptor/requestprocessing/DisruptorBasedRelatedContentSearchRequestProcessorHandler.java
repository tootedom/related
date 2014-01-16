package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedContentSearchRequestProcessorHandler.class);

    private volatile boolean shutdown = false;
    private final RelatedProductSearchExecutor searchRequestExecutor;
    private final RelatedProductSearchResultsToResponseGateway contextStorage;

    public DisruptorBasedRelatedContentSearchRequestProcessorHandler(RelatedProductSearchResultsToResponseGateway contextStorage,
                                                                     RelatedProductSearchExecutor searchExecutor) {
        this.searchRequestExecutor = searchExecutor;
        this.contextStorage = contextStorage;
    }

    @Override
    public void onEvent(RelatedProductSearchRequest event, long sequence, boolean endOfBatch) throws Exception {

        try {
//            AsyncContext clientContext = event.getRequestContext();
//            RelatedProductSearch search = RelatedProductSearchFactory.createAndPopulateSearchObject(configuration, event.getRequestType(), event.getRequestProperties());

//            RelatedProductSearch search = event.searchRequest;
//            event.setSearchExecutor(searchRequestExecutor);
//            asyncContextStorage.handleRequest(event,searchRequestExecutor);
//            searchRequestExecutor.executeSearch(search);
            handleRequest(event,searchRequestExecutor);

        } finally {
            event.getSearchRequest().setValidMessage(false);
//            event.setRequestProperties(null);
//            event.setRequestType(null);
        }

    }

    public void handleRequest(RelatedProductSearchRequest searchRequest, RelatedProductSearchExecutor searchExecutor) {
        contextStorage.storeResponseContextForSearchRequest(searchRequest.getSearchRequest().getLookupKey(), searchRequest.getRequestContexts());
        searchRequestExecutor.executeSearch(searchRequest.getSearchRequest());
    }


    public void shutdown() {
        if(!shutdown) {
            try {
                log.info("Shutting down response context gateway respository");
                contextStorage.shutdown();
            } catch(Exception e) {
                log.warn("Problem shutting down response context gateway respository");
            }

            try {
                log.info("Shutting down search request executor");
                searchRequestExecutor.shutdown();
            } catch(Exception e) {
                log.warn("Problem shutting down request executor");
            }
        }
    }

}
