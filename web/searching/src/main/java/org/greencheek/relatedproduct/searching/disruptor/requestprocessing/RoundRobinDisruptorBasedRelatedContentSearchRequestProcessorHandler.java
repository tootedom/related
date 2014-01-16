package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler{

    private static final Logger log = LoggerFactory.getLogger(RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler.class);


    private volatile boolean shutdown = false;

    private final RelatedProductSearchResultsToResponseGateway contextStorage;
    private final RelatedProductSearchExecutor[] searchRequestExecutor;

    private int currentIndex = 0;
    private final int mask;

    public RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(RelatedProductSearchResultsToResponseGateway contextStorage,
                                                                               RelatedProductSearchExecutor[] searchExecutor) {
        check(searchExecutor);
        this.contextStorage = contextStorage;
        this.searchRequestExecutor = searchExecutor;

        mask = searchExecutor.length-1;
    }

    private void check(RelatedProductSearchExecutor[] searchExecutor) throws InstantiationError {
        int length = searchExecutor.length;

        if(length != Util.ceilingNextPowerOfTwo(length)) {
            throw new InstantiationError("The length of the context lookup and search executors array must be a power of 2");
        }

    }

    @Override
    public void onEvent(RelatedProductSearchRequest event, long sequence, boolean endOfBatch) throws Exception {
        try {
            handleRequest(event,searchRequestExecutor[this.currentIndex++ & mask]);
        } finally {
            event.getSearchRequest().setValidMessage(false);

        }
    }

    public void handleRequest(RelatedProductSearchRequest searchRequest, RelatedProductSearchExecutor searchExecutor) {
        contextStorage.storeResponseContextForSearchRequest(searchRequest.getSearchRequest().getLookupKey(), searchRequest.getRequestContexts());
        searchExecutor.executeSearch(searchRequest.getSearchRequest());
    }


    public void shutdown() {
        if(!this.shutdown) {
            this.shutdown = true;

            for (RelatedProductSearchExecutor searchExecutor : searchRequestExecutor) {
                try {
                    searchExecutor.shutdown();
                } catch(Exception e) {
                    log.warn("Unable to stop SearchRequestExecutor");
                }
            }
        }
    }

}
