package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.searching.bootstrap.ApplicationCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinRelatedContentSearchRequestProcessorHandlerFactory implements RelatedContentSearchRequestProcessorHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(RoundRobinRelatedContentSearchRequestProcessorHandlerFactory.class);

    public RoundRobinRelatedContentSearchRequestProcessorHandlerFactory() {
    }

    @Override
    public RelatedContentSearchRequestProcessorHandler createHandler(Configuration config, ApplicationCtx appContext) {
        int numberOfSearchProcessors = config.getNumberOfSearchingRequestProcessors();
        if(numberOfSearchProcessors==1) {
            log.debug("Creating Single Search Request Processor");
            AsyncContextLookup asyncContextStorage = appContext.createAsyncContextLookup();
            RelatedProductSearchResultsResponseProcessor responseProcessor = appContext.createProcessorForSendingSearchResultsSendToClient();
            RelatedProductSearchRequestResponseProcessor requestAndResponseProcessor = appContext.createSearchRequestAndResponseGateway(asyncContextStorage,responseProcessor);
            RelatedProductSearchExecutor searchExecutor = appContext.createSearchExecutor(requestAndResponseProcessor);
            return new DisruptorBasedRelatedContentSearchRequestProcessorHandler(config,requestAndResponseProcessor,searchExecutor);
        } else {
            AsyncContextLookup asyncContextStorage = appContext.createAsyncContextLookup();
            RelatedProductSearchResultsResponseProcessor responseProcessor = appContext.createProcessorForSendingSearchResultsSendToClient();
            RelatedProductSearchRequestResponseProcessor requestAndResponseProcessor = appContext.createSearchRequestAndResponseGateway(asyncContextStorage,responseProcessor);

            numberOfSearchProcessors = Util.ceilingNextPowerOfTwo(numberOfSearchProcessors);

            log.debug("Creating {} Search Request Processor",numberOfSearchProcessors);
            RelatedProductSearchExecutor[] searchExecutors = new RelatedProductSearchExecutor[numberOfSearchProcessors];
            RelatedProductSearchRequestResponseProcessor[]  requestResponseGateways = new RelatedProductSearchRequestResponseProcessor[numberOfSearchProcessors];
            int i = numberOfSearchProcessors;
            while(i-- !=0) {
                searchExecutors[i] = appContext.createSearchExecutor(requestAndResponseProcessor);
                requestResponseGateways[i] = requestAndResponseProcessor;
            }

            return new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(config,requestResponseGateways,searchExecutors);
        }
    }
}
