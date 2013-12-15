package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.searching.bootstrap.BootstrapApplicationContext;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/12/2013
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class RoundRobinRelatedContentSearchRequestProcessorHandlerFactoryTest {


    @After
    public void tearDown() {
        System.clearProperty("related-product.number.of.searching.request.processors");
    }

    @Test
    public void testSingleRelatedContentSearchRequestProcessorHandlerIsCreated() {
        System.setProperty("related-product.number.of.searching.request.processors","1");
        Configuration config = new SystemPropertiesConfiguration();

        RoundRobinRelatedContentSearchRequestProcessorHandlerFactory factory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        RelatedContentSearchRequestProcessorHandler handler = factory.createHandler(config,mock(ApplicationCtx.class));

        assertTrue(handler instanceof DisruptorBasedRelatedContentSearchRequestProcessorHandler);
    }

    @Test
    public void testRoundRobinRelatedContentSearchRequestProcessorHandlerIsCreated() {
        System.setProperty("related-product.number.of.searching.request.processors","2");
        Configuration config = new SystemPropertiesConfiguration();

        RoundRobinRelatedContentSearchRequestProcessorHandlerFactory factory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        RelatedContentSearchRequestProcessorHandler handler = factory.createHandler(config,mock(ApplicationCtx.class));

        assertTrue(handler instanceof RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler);
    }


//    @Override
//    public RelatedContentSearchRequestProcessorHandler createHandler(Configuration config, ApplicationCtx appContext) {
//        int numberOfSearchProcessors = config.getNumberOfSearchingRequestProcessors();
//        RelatedProductSearchResultsResponseProcessor responseProcessor = appContext.createProcessorForSendingSearchResultsSendToClient();
//        RelatedProductSearchRequestResponseProcessor requestAndResponseProcessor = appContext.createSearchRequestAndResponseGateway(appContext.createAsyncContextLookup(),responseProcessor);
//
//        if(numberOfSearchProcessors==1) {
//            log.debug("Creating Single Search Request Processor");
//
//            RelatedProductSearchExecutor searchExecutor = appContext.createSearchExecutor(requestAndResponseProcessor);
//            return new DisruptorBasedRelatedContentSearchRequestProcessorHandler(config,requestAndResponseProcessor,searchExecutor);
//        } else {
//
//
//            numberOfSearchProcessors = Util.ceilingNextPowerOfTwo(numberOfSearchProcessors);
//
//            log.debug("Creating {} Search Request Processor",numberOfSearchProcessors);
//            RelatedProductSearchExecutor[] searchExecutors = new RelatedProductSearchExecutor[numberOfSearchProcessors];
//            RelatedProductSearchRequestResponseProcessor[]  requestResponseGateways = new RelatedProductSearchRequestResponseProcessor[numberOfSearchProcessors];
//            int i = numberOfSearchProcessors;
//            while(i-- !=0) {
//                searchExecutors[i] = appContext.createSearchExecutor(requestAndResponseProcessor);
//                requestResponseGateways[i] = requestAndResponseProcessor;
//            }
//
//            return new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(config,requestResponseGateways,searchExecutors);
//        }
//    }
}
