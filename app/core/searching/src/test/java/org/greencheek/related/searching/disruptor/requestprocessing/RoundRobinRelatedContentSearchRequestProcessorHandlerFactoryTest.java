package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.searching.RelatedItemSearchExecutorFactory;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
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

        RelatedContentSearchRequestProcessorHandler handler = factory.createHandler(config,mock(RelatedItemSearchResultsToResponseGateway.class),mock(RelatedItemSearchExecutorFactory.class));

        assertTrue(handler instanceof DisruptorBasedRelatedContentSearchRequestProcessorHandler);
    }

    @Test
    public void testRoundRobinRelatedContentSearchRequestProcessorHandlerIsCreated() {
        System.setProperty(ConfigurationConstants.PROPNAME_NUMBER_OF_SEARCHING_REQUEST_PROCESSORS,"2");
        Configuration config = new SystemPropertiesConfiguration();

        RoundRobinRelatedContentSearchRequestProcessorHandlerFactory factory = new RoundRobinRelatedContentSearchRequestProcessorHandlerFactory();

        RelatedContentSearchRequestProcessorHandler handler = factory.createHandler(config,mock(RelatedItemSearchResultsToResponseGateway.class),mock(RelatedItemSearchExecutorFactory.class));

        assertTrue(handler instanceof RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler);
    }

}
