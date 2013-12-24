package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidator;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 23/12/2013
 * Time: 20:39
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedSearchRequestProcessorTest {



    @Test
    public void testProcessRequestThatFailsValidationThrowsAnException() throws Exception {
        Configuration configuration = new SystemPropertiesConfiguration();
        IncomingSearchRequestTranslator translator = mock(IncomingSearchRequestTranslator.class);
        RelatedContentSearchRequestProcessorHandler hanlder = mock(RelatedContentSearchRequestProcessorHandler.class);
        SearchRequestParameterValidatorLocator validator = mock(SearchRequestParameterValidatorLocator.class);

        DisruptorBasedSearchRequestProcessor processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedProductSearchRequestFactory(configuration),configuration,validator);

    }
}
