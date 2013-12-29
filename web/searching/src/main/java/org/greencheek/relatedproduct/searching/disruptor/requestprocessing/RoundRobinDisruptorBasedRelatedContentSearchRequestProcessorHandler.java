package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;

/**
 *
 */
public class RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler{

    private static final Logger log = LoggerFactory.getLogger(RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler.class);


    private volatile boolean shutdown = false;

    private final Configuration configuration;
    private final RelatedProductSearchRequestResponseProcessor asyncContextStorage;
    private final RelatedProductSearchExecutor[] searchRequestExecutor;

    private int currentIndex = 0;
    private final int mask;

    public RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(Configuration configuration,
                                                                               RelatedProductSearchRequestResponseProcessor asyncContextStorage,
                                                                               RelatedProductSearchExecutor[] searchExecutor) {
        check(searchExecutor);
        this.configuration = configuration;
        this.asyncContextStorage = asyncContextStorage;
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
//            AsyncContext clientContext = event.getRequestContext();
//            RelatedProductSearch search = RelatedProductSearchFactory.populateSearchObject(configuration, event.searchRequest,event.getRequestType(), event.getRequestProperties());
//            RelatedProductSearch search = event.searchRequest;
            int currentIndex = this.currentIndex++ & mask;
//            event.setSearchExecutor(searchRequestExecutor[currentIndex]);
            asyncContextStorage.handleRequest(event, searchRequestExecutor[currentIndex]);

//            searchRequestExecutor[currentIndex].executeSearch(search);
        } finally {
            event.getSearchRequest().setValidMessage(false);

        }
    }

    public void shutdown() {
        if(!this.shutdown) {
            this.shutdown = true;

            try {
                asyncContextStorage.shutdown();
            } catch (Exception e) {
                log.warn("Unable to stop RequestAndRequest Gateway (AsyncContextStorage)");
            }


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
