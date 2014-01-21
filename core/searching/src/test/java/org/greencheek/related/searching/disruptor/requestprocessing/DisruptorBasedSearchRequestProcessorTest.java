package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.RelatedItemSearchRequestProcessor;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.domain.RelatedItemSearchRequestFactory;
import org.greencheek.related.searching.requestprocessing.*;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests that a request coming into the service can be validated and subsequently submitted or rejected
 * for processing.
 */
public class DisruptorBasedSearchRequestProcessorTest {

    RelatedItemSearchRequestProcessor processor;

    @After
    public void tearDown() {
        if(processor!=null) {
            processor.shutdown();
        }
    }

    @Test
    public void testProcessRequestThatFailsValidationIsCaughtAndNotProcessed() throws Exception {
        Configuration configuration = new SystemPropertiesConfiguration();
        IncomingSearchRequestTranslator translator = mock(IncomingSearchRequestTranslator.class);
        RelatedContentSearchRequestProcessorHandler hanlder = mock(RelatedContentSearchRequestProcessorHandler.class);
        SearchRequestParameterValidatorLocator validatorFactory = mock(SearchRequestParameterValidatorLocator.class);
        SearchRequestParameterValidator validator = mock(SearchRequestParameterValidator.class);
        ValidationMessage message = mock(ValidationMessage.class);

        when(message.isValid()).thenReturn(false);
        when(validator.validateParameters(anyMap())).thenReturn(message);
        when(validatorFactory.getValidatorForType(RelatedItemSearchType.FREQUENTLY_RELATED_WITH)).thenReturn(validator);

        processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedItemSearchRequestFactory(configuration),configuration,validatorFactory);

        // No id present in request
        SearchRequestSubmissionStatus status = processor.processRequest(RelatedItemSearchType.FREQUENTLY_RELATED_WITH,new HashMap<String,String>(),null);

        assertEquals(SearchRequestSubmissionStatus.REQUEST_VALIDATION_FAILURE,status);

        // Verify that the translator is not called.  If it was, this means the validation error was not caught
        verify(translator, times(0)).translateTo(any(RelatedItemSearchRequest.class), anyLong(),
                any(RelatedItemSearchType.class), anyMap(), any(SearchResponseContext[].class));

    }

    @Test
    public void testProcessRequestFailsValidationDueToMissingId() {
        Configuration configuration = new SystemPropertiesConfiguration();
        IncomingSearchRequestTranslator translator = mock(IncomingSearchRequestTranslator.class);


        RelatedContentSearchRequestProcessorHandler hanlder = mock(RelatedContentSearchRequestProcessorHandler.class);
        SearchRequestParameterValidatorLocator validatorFactory = new MapBasedSearchRequestParameterValidatorLookup(configuration);

        processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedItemSearchRequestFactory(configuration),configuration,validatorFactory);

        // No id present in request
        SearchRequestSubmissionStatus status = processor.processRequest(RelatedItemSearchType.FREQUENTLY_RELATED_WITH,new HashMap<String,String>(),null);

        assertEquals(SearchRequestSubmissionStatus.REQUEST_VALIDATION_FAILURE,status);

        // Verify that the translator is not called.  If it was, this means the validation error was not caught
        verify(translator, times(0)).translateTo(any(RelatedItemSearchRequest.class), anyLong(),
                any(RelatedItemSearchType.class), anyMap(), any(SearchResponseContext[].class));
    }

    @Test
    public void testProcessorCalledAfterValidationSuccess() {
        Configuration configuration = new SystemPropertiesConfiguration();
        IncomingSearchRequestTranslator translator = mock(IncomingSearchRequestTranslator.class);

        RelatedContentSearchRequestProcessorHandler hanlder = mock(RelatedContentSearchRequestProcessorHandler.class);
        SearchRequestParameterValidatorLocator validatorFactory = new MapBasedSearchRequestParameterValidatorLookup(configuration);

        processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedItemSearchRequestFactory(configuration),configuration,validatorFactory);

        Map<String,String> requestParameters = new HashMap<String,String>();
        requestParameters.put(configuration.getRequestParameterForId(),"1");

        // No id present in request
        SearchRequestSubmissionStatus status = processor.processRequest(RelatedItemSearchType.FREQUENTLY_RELATED_WITH,requestParameters,null);

        assertEquals(SearchRequestSubmissionStatus.PROCESSING,status);

        // Verify that the translator is not called.  If it was, this means the validation error was not caught
        verify(translator, times(1)).translateTo(any(RelatedItemSearchRequest.class), anyLong(),
                any(RelatedItemSearchType.class), anyMap(), any(SearchResponseContext[].class));
    }
}
