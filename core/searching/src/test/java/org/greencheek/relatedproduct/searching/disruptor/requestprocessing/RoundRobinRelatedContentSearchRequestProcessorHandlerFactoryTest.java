package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutorFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.ConfigurationConstants;
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
        System.clearProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS);
    }

    @Test
    public void testSingleRelatedContentSearchRequestProcessorHandlerIsCreated() {
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,"1");
        Configuration config = new SystemPropertiesConfiguration();

        RoundRobinRelatedContentSearchRequestProcessorHandlerFactory factory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        RelatedContentSearchRequestProcessorHandler handler = factory.createHandler(config,mock(RelatedProductSearchResultsToResponseGateway.class),mock(RelatedProductSearchExecutorFactory.class));

        assertTrue(handler instanceof DisruptorBasedRelatedContentSearchRequestProcessorHandler);
    }

    @Test
    public void testRoundRobinRelatedContentSearchRequestProcessorHandlerIsCreated() {
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,"2");
        Configuration config = new SystemPropertiesConfiguration();

        RoundRobinRelatedContentSearchRequestProcessorHandlerFactory factory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        RelatedContentSearchRequestProcessorHandler handler = factory.createHandler(config,mock(RelatedProductSearchResultsToResponseGateway.class),mock(RelatedProductSearchExecutorFactory.class));

        assertTrue(handler instanceof RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler);
    }

}
