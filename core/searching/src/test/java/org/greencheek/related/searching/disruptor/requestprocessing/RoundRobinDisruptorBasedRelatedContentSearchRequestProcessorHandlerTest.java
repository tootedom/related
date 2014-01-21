package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
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
        RelatedItemSearchExecutor executor1 = mock(RelatedItemSearchExecutor.class);
        RelatedItemSearchExecutor executor2 = mock(RelatedItemSearchExecutor.class);
        RelatedItemSearchExecutor executor3 = mock(RelatedItemSearchExecutor.class);

        RelatedItemSearchResultsToResponseGateway gateway = mock(RelatedItemSearchResultsToResponseGateway.class);

        handler = new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,new RelatedItemSearchExecutor[]{executor1,executor2,executor3});
    }

    @Test
    public void testCallingOnEventThatEachExecutorIsCalledInRoundRobinFashion() throws Exception {
        Configuration config = new SystemPropertiesConfiguration();
        RelatedItemSearchExecutor executor1 = mock(RelatedItemSearchExecutor.class);
        RelatedItemSearchExecutor executor2 = mock(RelatedItemSearchExecutor.class);
        RelatedItemSearchExecutor executor3 = mock(RelatedItemSearchExecutor.class);
        RelatedItemSearchExecutor executor4 = mock(RelatedItemSearchExecutor.class);

        RelatedItemSearchResultsToResponseGateway gateway = mock(RelatedItemSearchResultsToResponseGateway.class);

        handler = new RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(gateway,new RelatedItemSearchExecutor[]{executor1,executor2,executor3,executor4});

        RelatedItemSearchRequest r1 = new RelatedItemSearchRequest(config);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("1"));
        handler.onEvent(r1,1,true);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("2"));

        handler.onEvent(r1,1,true);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("3"));

        handler.onEvent(r1,1,true);
        r1.getSearchRequest().setLookupKey(new SipHashSearchRequestLookupKey("4"));

        handler.onEvent(r1,1,true);

        verify(executor1,times(1)).executeSearch(any(RelatedItemSearch.class));
        verify(executor2,times(1)).executeSearch(any(RelatedItemSearch.class));
        verify(executor3,times(1)).executeSearch(any(RelatedItemSearch.class));
        verify(executor4,times(1)).executeSearch(any(RelatedItemSearch.class));
    }


}
