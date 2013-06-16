package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedSearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.SearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorBasedResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.DisruptorBasedRelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.RelatedProductSearchDisruptorEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.RelatedProductSearchEventHandler;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.HttpBasedRelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.web.ApplicationCtx;
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
@Named
public class RoundRobinRelatedContentSearchRequestProcessorHandlerFactory implements RelatedContentSearchRequestProcessorHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(RoundRobinRelatedContentSearchRequestProcessorHandlerFactory.class);

    private final Configuration config;

    @Inject
    public RoundRobinRelatedContentSearchRequestProcessorHandlerFactory(Configuration configuration) {
        this.config = configuration;
    }

    @Override
    public RelatedContentSearchRequestProcessorHandler createHandler(ApplicationCtx appContext) {
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
