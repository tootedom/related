package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.searching.bootstrap.ApplicationCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        SearchResponseContextLookup lookup = appContext.createAsyncContextLookup();
        int numberOfSearchProcessors = config.getNumberOfSearchingRequestProcessors();
        RelatedProductSearchResultsResponseProcessor responseProcessor = appContext.createProcessorForSendingSearchResultsSendToClient(lookup);
//        RelatedProductSearchResponseProcessor requestAndResponseProcessor = appContext.createSearchRequestAndResponseGateway(lookup,responseProcessor);

        if(numberOfSearchProcessors==1) {
            log.debug("Creating Single Search Request Processor");

            RelatedProductSearchExecutor searchExecutor = appContext.createSearchExecutor(responseProcessor);
            return new DisruptorBasedRelatedContentSearchRequestProcessorHandler(lookup,searchExecutor);
        } else {


            numberOfSearchProcessors = Util.ceilingNextPowerOfTwo(numberOfSearchProcessors);

            log.debug("Creating {} Search Request Processor",numberOfSearchProcessors);
            RelatedProductSearchExecutor[] searchExecutors = new RelatedProductSearchExecutor[numberOfSearchProcessors];
            int i = numberOfSearchProcessors;
            while(i-- !=0) {
                searchExecutors[i] = appContext.createSearchExecutor(responseProcessor);
            }

            return new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(lookup,searchExecutors);
        }
    }
}
