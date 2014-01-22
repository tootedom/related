package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedContentSearchRequestProcessorHandler.class);

    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private final RelatedItemSearchExecutor searchRequestExecutor;
    private final RelatedItemSearchResultsToResponseGateway contextStorage;

    public DisruptorBasedRelatedContentSearchRequestProcessorHandler(RelatedItemSearchResultsToResponseGateway contextStorage,
                                                                     RelatedItemSearchExecutor searchExecutor) {
        this.searchRequestExecutor = searchExecutor;
        this.contextStorage = contextStorage;
    }

    @Override
    public void onEvent(RelatedItemSearchRequest event, long sequence, boolean endOfBatch) throws Exception {
        handleRequest(event,searchRequestExecutor);
    }

    public void handleRequest(RelatedItemSearchRequest searchRequest, RelatedItemSearchExecutor searchExecutor) {
        contextStorage.storeResponseContextForSearchRequest(searchRequest.getSearchRequest().getLookupKey(), searchRequest.getRequestContexts());
        searchRequestExecutor.executeSearch(searchRequest.getSearchRequest());
    }


    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {

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
