package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler{

    private volatile boolean shutdown = false;

    private final Configuration configuration;
    private final RelatedProductSearchRequestResponseProcessor asyncContextStorage;
    private final RelatedProductSearchExecutor searchRequestExecutor;

    public DisruptorBasedRelatedContentSearchRequestProcessorHandler(Configuration configuration,
                                                                     RelatedProductSearchRequestResponseProcessor asyncContextStorage,
                                                                     RelatedProductSearchExecutor searchExecutor) {
        this.configuration = configuration;
        this.asyncContextStorage = asyncContextStorage;
        this.searchRequestExecutor = searchExecutor;
    }

    @Override
    public void onEvent(RelatedProductSearchRequest event, long sequence, boolean endOfBatch) throws Exception {

        try {
            AsyncContext clientContext = event.getRequestContext();
//            RelatedProductSearch search = RelatedProductSearchFactory.createAndPopulateSearchObject(configuration, event.getRequestType(), event.getRequestProperties());

            RelatedProductSearch search = event.searchRequest;
            asyncContextStorage.handleRequest(search.getLookupKey(),clientContext);
            searchRequestExecutor.executeSearch(search);
        } finally {
            event.setRequestContext(null);
            event.searchRequest.setValidMessage(false);
//            event.setRequestProperties(null);
//            event.setRequestType(null);
        }

    }

    public void shutdown() {
        this.shutdown = true;
    }

}
