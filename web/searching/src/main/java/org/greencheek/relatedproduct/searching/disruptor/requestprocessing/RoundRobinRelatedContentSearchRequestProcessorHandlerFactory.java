package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.util.arrayindexing.Util;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.searching.web.bootstrap.ApplicationCtx;
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

        RelatedProductSearchResultsToResponseGateway gateway = appContext.getSearchResultsToReponseGateway();
        int numberOfSearchProcessors = config.getNumberOfSearchingRequestProcessors();

        if(numberOfSearchProcessors==1) {
            log.debug("Creating Single Search Request Processor");

            RelatedProductSearchExecutor searchExecutor = appContext.createSearchExecutor(gateway);
            return new DisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,searchExecutor);
        } else {


            numberOfSearchProcessors = Util.ceilingNextPowerOfTwo(numberOfSearchProcessors);

            log.debug("Creating {} Search Request Processor",numberOfSearchProcessors);
            RelatedProductSearchExecutor[] searchExecutors = new RelatedProductSearchExecutor[numberOfSearchProcessors];
            int i = numberOfSearchProcessors;
            while(i-- !=0) {
                searchExecutors[i] = appContext.createSearchExecutor(gateway);
            }

            return new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,searchExecutors);
        }
    }
}
