package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestProcessor;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.requestprocessing.*;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
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

    RelatedProductSearchRequestProcessor processor;

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
        when(validatorFactory.getValidatorForType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH)).thenReturn(validator);

        processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedProductSearchRequestFactory(configuration),configuration,validatorFactory);

        // No id present in request
        SearchRequestSubmissionStatus status = processor.processRequest(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,new HashMap<String,String>(),null);

        assertEquals(SearchRequestSubmissionStatus.REQUEST_VALIDATION_FAILURE,status);

        // Verify that the translator is not called.  If it was, this means the validation error was not caught
        verify(translator, times(0)).translateTo(any(RelatedProductSearchRequest.class), anyLong(),
                any(RelatedProductSearchType.class), anyMap(), any(SearchResponseContext[].class));

    }

    @Test
    public void testProcessRequestFailsValidationDueToMissingId() {
        Configuration configuration = new SystemPropertiesConfiguration();
        IncomingSearchRequestTranslator translator = mock(IncomingSearchRequestTranslator.class);


        RelatedContentSearchRequestProcessorHandler hanlder = mock(RelatedContentSearchRequestProcessorHandler.class);
        SearchRequestParameterValidatorLocator validatorFactory = new MapBasedSearchRequestParameterValidatorLookup(configuration);

        processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedProductSearchRequestFactory(configuration),configuration,validatorFactory);

        // No id present in request
        SearchRequestSubmissionStatus status = processor.processRequest(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,new HashMap<String,String>(),null);

        assertEquals(SearchRequestSubmissionStatus.REQUEST_VALIDATION_FAILURE,status);

        // Verify that the translator is not called.  If it was, this means the validation error was not caught
        verify(translator, times(0)).translateTo(any(RelatedProductSearchRequest.class), anyLong(),
                any(RelatedProductSearchType.class), anyMap(), any(SearchResponseContext[].class));
    }

    @Test
    public void testProcessorCalledAfterValidationSuccess() {
        Configuration configuration = new SystemPropertiesConfiguration();
        IncomingSearchRequestTranslator translator = mock(IncomingSearchRequestTranslator.class);

        RelatedContentSearchRequestProcessorHandler hanlder = mock(RelatedContentSearchRequestProcessorHandler.class);
        SearchRequestParameterValidatorLocator validatorFactory = new MapBasedSearchRequestParameterValidatorLookup(configuration);

        processor = new DisruptorBasedSearchRequestProcessor(translator,
                hanlder,new RelatedProductSearchRequestFactory(configuration),configuration,validatorFactory);

        Map<String,String> requestParameters = new HashMap<String,String>();
        requestParameters.put(configuration.getRequestParameterForId(),"1");

        // No id present in request
        SearchRequestSubmissionStatus status = processor.processRequest(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,requestParameters,null);

        assertEquals(SearchRequestSubmissionStatus.PROCESSING,status);

        // Verify that the translator is not called.  If it was, this means the validation error was not caught
        verify(translator, times(1)).translateTo(any(RelatedProductSearchRequest.class), anyLong(),
                any(RelatedProductSearchType.class), anyMap(), any(SearchResponseContext[].class));
    }
}
