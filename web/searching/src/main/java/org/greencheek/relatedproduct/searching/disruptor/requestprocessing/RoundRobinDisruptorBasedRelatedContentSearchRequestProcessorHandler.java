package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler{

    private static final Logger log = LoggerFactory.getLogger(RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler.class);


    private volatile boolean shutdown = false;

    private final Configuration configuration;
    private final RelatedProductSearchRequestResponseProcessor[] asyncContextStorage;
    private final RelatedProductSearchExecutor[] searchRequestExecutor;

    private int currentIndex = 0;
    private final int mask;

    public RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(Configuration configuration,
                                                                               RelatedProductSearchRequestResponseProcessor[] asyncContextStorage,
                                                                               RelatedProductSearchExecutor[] searchExecutor) {
        check(asyncContextStorage, searchExecutor);
        this.configuration = configuration;
        this.asyncContextStorage = asyncContextStorage;
        this.searchRequestExecutor = searchExecutor;

        mask = asyncContextStorage.length-1;

    }

    private void check( RelatedProductSearchRequestResponseProcessor[] asyncContextStorage,
                        RelatedProductSearchExecutor[] searchExecutor) throws InstantiationError {
        if(asyncContextStorage.length!=searchExecutor.length) {
            throw new InstantiationError("AsyncContextLookup array must be the same size as the searchExecutors array");
        }

        int length = asyncContextStorage.length;

        if(length != Util.ceilingNextPowerOfTwo(length)) {
            throw new InstantiationError("The length of the context lookup and search executors array must be a power of 2");
        }

    }

    @Override
    public void onEvent(RelatedProductSearchRequest event, long sequence, boolean endOfBatch) throws Exception {
        try {
            AsyncContext clientContext = event.getRequestContext();
            RelatedProductSearch search = RelatedProductSearchFactory.createAndPopulateSearchObject(configuration, event.getRequestType(), event.getRequestProperties());
            int currentIndex = this.currentIndex++ & mask;
            asyncContextStorage[currentIndex].handleRequest(search.getLookupKey(configuration),clientContext);
            searchRequestExecutor[currentIndex].executeSearch(search);
        } finally {
            event.setRequestContext(null);
            event.setRequestProperties(null);
            event.setRequestType(null);
        }
    }

    public void shutdown() {
        if(!this.shutdown) {
            this.shutdown = true;

            for(RelatedProductSearchRequestResponseProcessor processor : asyncContextStorage) {
                try {
                    processor.shutdown();
                } catch (Exception e) {
                    log.warn("Unable to stop RequestAndRequest Gateway (AsyncContextStorage)");
                }
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
