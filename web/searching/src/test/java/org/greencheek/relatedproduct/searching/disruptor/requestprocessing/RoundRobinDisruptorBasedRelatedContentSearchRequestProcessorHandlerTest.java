package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.DisruptorRelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by dominictootell on 07/01/2014.
 */
public class RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandlerTest {

    RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler handler;


    @After
    public void tearDown() {
        if(handler!=null) {
            handler.shutdown();
        }
    }




    @Test(expected = InstantiationError.class)
    public void testExceptionThrowWhenAPowerOfTwoNumberOfExecutorsIsntPassed() {
        Configuration config = new SystemPropertiesConfiguration();
        RelatedProductSearchExecutor executor1 = mock(RelatedProductSearchExecutor.class);
        RelatedProductSearchExecutor executor2 = mock(RelatedProductSearchExecutor.class);
        RelatedProductSearchExecutor executor3 = mock(RelatedProductSearchExecutor.class);

        RelatedProductSearchResultsToResponseGateway gateway = mock(RelatedProductSearchResultsToResponseGateway.class);

        handler = new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,new RelatedProductSearchExecutor[]{executor1,executor2,executor3});
    }

    @Test
    public void testCallingOnEventThatEachExecutorIsCalledInRoundRobinFashion() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();
        RelatedProductSearchExecutor executor1 = mock(RelatedProductSearchExecutor.class);
        RelatedProductSearchExecutor executor2 = mock(RelatedProductSearchExecutor.class);
        RelatedProductSearchExecutor executor3 = mock(RelatedProductSearchExecutor.class);
        RelatedProductSearchExecutor executor4 = mock(RelatedProductSearchExecutor.class);

        RelatedProductSearchResultsToResponseGateway gateway = mock(RelatedProductSearchResultsToResponseGateway.class);

        handler = new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,new RelatedProductSearchExecutor[]{executor1,executor2,executor3,executor4});

        RelatedProductSearchRequest r1 = new RelatedProductSearchRequest(config);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("1"));
        handler.onEvent(r1,1,true);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("2"));

        handler.onEvent(r1,1,true);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("3"));

        handler.onEvent(r1,1,true);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("4"));

        handler.onEvent(r1,1,true);

        verify(executor1,times(1)).executeSearch(any(RelatedProductSearch.class));
        verify(executor2,times(1)).executeSearch(any(RelatedProductSearch.class));
        verify(executor3,times(1)).executeSearch(any(RelatedProductSearch.class));
        verify(executor4,times(1)).executeSearch(any(RelatedProductSearch.class));
    }


}
