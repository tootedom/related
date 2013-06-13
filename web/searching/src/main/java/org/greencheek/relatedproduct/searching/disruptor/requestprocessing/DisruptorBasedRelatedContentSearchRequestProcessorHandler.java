package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.SearchRequestResponseHandler;
import org.greencheek.relatedproduct.util.config.Configuration;

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
@Named
public class DisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler{

    private volatile boolean shutdown = false;

    private final Configuration configuration;
    private final SearchRequestResponseHandler asyncContextStorage;
    private final RelatedProductSearchExecutor searchRequestExecutor;

    @Inject
    public DisruptorBasedRelatedContentSearchRequestProcessorHandler(Configuration configuration,
                                                                     SearchRequestResponseHandler asyncContextStorage,
                                                                     RelatedProductSearchExecutor searchExecutor) {
        this.configuration = configuration;
        this.asyncContextStorage = asyncContextStorage;
        this.searchRequestExecutor = searchExecutor;
    }

    @Override
    public void onEvent(RelatedProductSearchRequest event, long sequence, boolean endOfBatch) throws Exception {
        if(shutdown) return;
        AsyncContext clientContext = event.getRequestContext();
        RelatedProductSearch search = RelatedProductSearchFactory.createAndPopulateSearchObject(configuration, event.getRequestType(), event.getRequestProperties());

        asyncContextStorage.handleRequest(search.getLookupKey(configuration),clientContext);
        searchRequestExecutor.executeSearch(search);
    }

    public void shutdown() {
        this.shutdown = true;
    }

}
