package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.domain.searching.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedRequestResponseProcessor;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.DisruptorBasedSearchEventHandler;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.SearchEventHandler;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.*;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapAsyncContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.AsyncContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * More of an integration test.  Checking that the wire of the components works
 */
public class DisruptorBasedRelatedProductSearchExecutorTest {


    private Configuration configuration = new SystemPropertiesConfiguration();
    RelatedProductSearchDisruptorEventHandler eventHandler;
    RelatedProductSearchRepository searchRepositoryWith2SecondDelay;
    SearchResultEventWithSearchRequestKey[] searchResults;
    RelatedProductSearchRequestResponseProcessor requestResponseProcessor;
    AsyncContextLookup contextLookup;
    SearchEventHandler handler;
    RelatedProductSearchFactory relatedProductSearchFactory;

    RelatedProductSearchExecutor searchExecutor;
    RelatedProductSearchResultsResponseProcessor responseProcessor;

    AtomicInteger searchRepositoryCallCount;
    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatch searchRequestPerformed = new CountDownLatch(1);
    SearchRequestLookupKey key = new SipHashSearchRequestLookupKey("1");

    @After
    public void tearDown() {
        try {
            requestResponseProcessor.shutdown();
        } catch(Exception e) {

        }

        try {
            searchExecutor.shutdown();
        } catch(Exception e) {

        }
    }

    @Before
    public void setUp() {
        searchRepositoryCallCount = new AtomicInteger(0);
        responseProcessor = mock(RelatedProductSearchResultsResponseProcessor.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                latch.countDown();
                return null;
            }
        }).when(responseProcessor).processSearchResults(any(AsyncContext[].class), any(SearchResultsEvent.class));

        searchRepositoryWith2SecondDelay = mock(RelatedProductSearchRepository.class);
        contextLookup = new MultiMapAsyncContextLookup(configuration);
        searchResults = createResults();

        // Simulate a delay.  This is so that request contexts can build
        // up on the contextLookup storage.
        when(searchRepositoryWith2SecondDelay.findRelatedProducts(any(Configuration.class),any(RelatedProductSearch[].class))).thenAnswer(new Answer<SearchResultEventWithSearchRequestKey[]>() {
            @Override
            public SearchResultEventWithSearchRequestKey[] answer(InvocationOnMock invocation) throws Throwable {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {

                }
                searchRequestPerformed.countDown();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {

                }
                searchRepositoryCallCount.incrementAndGet();
                return searchResults;
            }
        });

        // handling servicing requests and responses
        handler = new DisruptorBasedSearchEventHandler(configuration,contextLookup,responseProcessor);

        //
        // Central entry point for the test.  We send requests here, and confirm the responseProcessor was called
        // and that the searchExecutor was called once.
        // The response will come back to here.
        requestResponseProcessor = new DisruptorBasedRequestResponseProcessor(handler, configuration);

        // Performs the search, and sends the response to the requestResponseProcessor
        eventHandler = new RelatedProductSearchEventHandler(configuration, searchRepositoryWith2SecondDelay,requestResponseProcessor);

        relatedProductSearchFactory = mock(RelatedProductSearchFactory.class);
        when(relatedProductSearchFactory.newInstance()).thenReturn(new RelatedProductSearch(configuration));

        // ring buffer that passes RelateProductSearch to the eventHandler
        searchExecutor = new DisruptorBasedRelatedProductSearchExecutor(configuration,relatedProductSearchFactory,eventHandler);

    }


    @Test
    public void testMultipleLookupsForSameKeyResultsInOnSearchRequest() {
        requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
        requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
        requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
        requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
        requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
        try {
            boolean completed = searchRequestPerformed.await(3000,TimeUnit.MILLISECONDS);
            if(!completed) {
                fail("Timeout waiting for search results to be returned");
            }
            requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
            requestResponseProcessor.handleRequest(createSearchRequest(searchExecutor));
        } catch(Exception e) {
            fail("Interrupted during waiting for search request to be performed");
        }

        try {
            boolean completed = latch.await(5000, TimeUnit.MILLISECONDS);
            if(!completed) {
                fail("fail ");
            }
        } catch(Exception e) {

        }


        assertEquals(1,searchRepositoryCallCount.get());
        assertTrue(contextLookup.addContext(key,mock(AsyncContext.class)));

        verify(responseProcessor).processSearchResults(argThat(new GivenAnArrayOfAsyncContextsOfLength(7)),any(SearchResultsEvent.class));
    }

    private class GivenAnArrayOfAsyncContextsOfLength extends ArgumentMatcher<AsyncContext[]> {

        private int argLength;
        public GivenAnArrayOfAsyncContextsOfLength(int length) {
            argLength = length;
        }

        @Override
        public boolean matches(Object argument) {
            if(argument instanceof AsyncContext[]) {
                AsyncContext[] contexts = (AsyncContext[])argument;
                return contexts.length==argLength;
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void describeTo(Description description) {
            super.describeTo(description);
            description.appendText(" "+argLength);
        }



        }

    @Test
    public void testShutdown() {

        requestResponseProcessor.shutdown();
        eventHandler.shutdown();
        verify(searchRepositoryWith2SecondDelay, times(1)).shutdown();
    }


    private RelatedProductSearchRequest createSearchRequest(RelatedProductSearchExecutor searchExecutor) {
        RelatedProductSearchRequest request = new RelatedProductSearchRequest(configuration);
        request.setSearchExecutor(searchExecutor);
        request.setRequestContext(mock(AsyncContext.class));
        request.getSearchRequest().setLookupKey(key);
        request.getSearchRequest().setMaxResults(10);
        request.getSearchRequest().setRelatedContentId("1");
        request.getSearchRequest().setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);
        request.getSearchRequest().setValidMessage(true);
        request.getSearchRequest().getAdditionalSearchCriteria().addProperty("channel","uk");
        request.getSearchRequest().getAdditionalSearchCriteria().addProperty("site","amazon");
        return request;
    }

    /**
     * Creates a search request for the key "1"
     * @param executor
     * @return
     */
    private SearchEvent getSearchRequest(RelatedProductSearchExecutor executor) {
        SearchEvent event = new SearchEvent();
        event.setEventType(SearchEventType.SEARCH_REQUEST);
        event.setRequestKey(key);
        event.setSearchRequestEvent(new SearchRequestEvent(mock(AsyncContext.class),mock(RelatedProductSearch.class),executor));
        return event;
    }

    /**
     * Creates a search result for the key "1"
     * @return
     */
    private SearchResultEventWithSearchRequestKey[] createResults() {
        SearchResultEventWithSearchRequestKey[] results = new SearchResultEventWithSearchRequestKey[1];

        results[0] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,
               new SipHashSearchRequestLookupKey("1"));

        return results;
    }



}
