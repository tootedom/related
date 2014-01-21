package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.RelatedItemSearchExecutorFactory;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.util.arrayindexing.Util;
import org.greencheek.related.util.config.Configuration;
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
    public RelatedContentSearchRequestProcessorHandler createHandler(Configuration config, RelatedItemSearchResultsToResponseGateway gateway,RelatedItemSearchExecutorFactory searchExecutorFactory ) {

        int numberOfSearchProcessors = config.getNumberOfSearchingRequestProcessors();

        if(numberOfSearchProcessors==1) {
            log.debug("Creating Single Search Request Processor");

            RelatedItemSearchExecutor searchExecutor = searchExecutorFactory.createSearchExecutor(gateway);
            return new DisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,searchExecutor);
        } else {


            numberOfSearchProcessors = Util.ceilingNextPowerOfTwo(numberOfSearchProcessors);

            log.debug("Creating {} Search Request Processor",numberOfSearchProcessors);
            RelatedItemSearchExecutor[] searchExecutors = new RelatedItemSearchExecutor[numberOfSearchProcessors];
            int i = numberOfSearchProcessors;
            while(i-- !=0) {
                searchExecutors[i] = searchExecutorFactory.createSearchExecutor(gateway);
            }

            return new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,searchExecutors);
        }
    }
}
