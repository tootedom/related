package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.DisruptorBasedRelatedContentSearchRequestProcessorHandler;
import org.greencheek.relatedproduct.searching.disruptor.requestprocessing.RelatedContentSearchRequestProcessorHandler;
import org.greencheek.relatedproduct.searching.disruptor.responseprocessing.*;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.MultiMapSearchResponseContextLookup;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextLookup;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * More of an integration test.  Checking that the wire of the components works
 */
public class DisruptorBasedRelatedProductSearchExecutorTest {
//

    private Configuration configuration = new SystemPropertiesConfiguration();
    RelatedProductSearchDisruptorEventHandler eventHandler;
    RelatedProductSearchRepository searchRepositoryWith2SecondDelay;
    SearchResultEventWithSearchRequestKey[] searchResults;
    SearchResponseContextLookup contextLookup;
    RelatedProductSearchFactory relatedProductSearchFactory;
    RelatedContentSearchRequestProcessorHandler requestProcessorHandler;

    RelatedProductSearchExecutor searchExecutor;
    ResponseEventHandler responseEventHandler;
    RelatedProductSearchResultsToResponseGateway responseProcessorGateway;
    SearchEventProcessor searchRequestEventProcessor;
    SearchEventProcessor searchResponseEventProcessor;


    AtomicInteger searchRepositoryCallCount;
    CountDownLatch latch = new CountDownLatch(1);
    CountDownLatch searchRequestPerformed = new CountDownLatch(1);
    SearchRequestLookupKey key = new SipHashSearchRequestLookupKey("1");
//
    @After
    public void tearDown() {
        try {
            searchExecutor.shutdown();
        } catch(Exception e) {

        }
    }

    @Before
    public void setUp() {

        searchRepositoryCallCount = new AtomicInteger(0);

        responseEventHandler = mock(ResponseEventHandler.class);


        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                latch.countDown();
                return null;
            }
        }).when(responseEventHandler).handleResponseEvents(any(SearchResultsEvent[].class), any(SearchResponseContext[][].class));


        searchRepositoryWith2SecondDelay = mock(RelatedProductSearchRepository.class);
        contextLookup = new MultiMapSearchResponseContextLookup(configuration);
        searchResults = createResults();

        searchRequestEventProcessor = new RequestSearchEventProcessor(contextLookup);
        searchResponseEventProcessor = new ResponseSearchEventProcessor(contextLookup,responseEventHandler);

        responseProcessorGateway = new DisruptorRelatedProductSearchResultsToResponseGateway(configuration,
                            searchRequestEventProcessor,searchResponseEventProcessor);



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

                // Slow down the return of the response, by slowing
                // down the response we allow more searches to come into the ring
                // buffer
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                searchRepositoryCallCount.incrementAndGet();
                return searchResults;
            }
        });

        // Performs the search, and sends the response to the request response gateway
        eventHandler = new RelatedProductSearchEventHandler(configuration, searchRepositoryWith2SecondDelay,responseProcessorGateway);

        relatedProductSearchFactory = mock(RelatedProductSearchFactory.class);
        when(relatedProductSearchFactory.newInstance()).thenReturn(new RelatedProductSearch(configuration));

        // ring buffer that passes RelateProductSearch to the eventHandler
        searchExecutor = new DisruptorBasedRelatedProductSearchExecutor(configuration,relatedProductSearchFactory,eventHandler);

        requestProcessorHandler = new DisruptorBasedRelatedContentSearchRequestProcessorHandler(responseProcessorGateway,searchExecutor);
    }
//
//
    @Test
    public void testMultipleLookupsForSameKeyResultsInOneSearchRequest() {
        requestProcessorHandler.handleRequest(createSearchRequest(),searchExecutor);
        requestProcessorHandler.handleRequest(createSearchRequest(),searchExecutor);
        requestProcessorHandler.handleRequest(createSearchRequest(), searchExecutor);
        requestProcessorHandler.handleRequest(createSearchRequest(),searchExecutor);
        requestProcessorHandler.handleRequest(createSearchRequest(),searchExecutor);
        try {
            boolean completed = searchRequestPerformed.await(3000, TimeUnit.MILLISECONDS);
            if(!completed) {
                fail("Timeout waiting for search results to be returned");
            }
            requestProcessorHandler.handleRequest(createSearchRequest(),searchExecutor);
            requestProcessorHandler.handleRequest(createSearchRequest(),searchExecutor);
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
        assertTrue(contextLookup.addContext(key,new SearchResponseContext[0]));

        try {
            verify(responseEventHandler,times(1)).handleResponseEvents(any(SearchResultsEvent[].class), any(SearchResponseContext[][].class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private class GivenAnArrayOfSearchResultEventWithSearchRequestKeyObjectWithSize extends ArgumentMatcher<SearchResultEventWithSearchRequestKey[]> {
//        private int argLength;
//        public GivenAnArrayOfSearchResultEventWithSearchRequestKeyObjectWithSize(int length) {
//            argLength = length;
//        }
//
//        @Override
//        public boolean matches(Object argument) {
//            if(argument instanceof SearchResponseContextHolder[]) {
//                SearchResponseContextHolder[] contexts = (SearchResponseContextHolder[])argument;
//                return contexts.length==argLength;
//            }
//            return false;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        public void describeTo(Description description) {
//            super.describeTo(description);
//            description.appendText(" "+argLength);
//        }
//    }
//
//
//    @Test
//    public void testShutdown() {
//
//        responseProcessor.shutdown();
//        eventHandler.shutdown();
//        verify(searchRepositoryWith2SecondDelay, times(1)).shutdown();
//    }
//
////


    /**
     * Creates a simple search request that would be created by the first entry point into the
     * system
     *
     * @return
     */
    private RelatedProductSearchRequest createSearchRequest() {
        RelatedProductSearchRequest request = new RelatedProductSearchRequest(configuration);
        request.getSearchRequest().setLookupKey(key);
        request.getSearchRequest().setMaxResults(10);
        request.getSearchRequest().setRelatedContentId("1");
        request.getSearchRequest().setRelatedProductSearchType(RelatedProductSearchType.FREQUENTLY_RELATED_WITH);
        request.getSearchRequest().setValidMessage(true);
        request.getSearchRequest().getAdditionalSearchCriteria().addProperty("channel","uk");
        request.getSearchRequest().getAdditionalSearchCriteria().addProperty("site","amazon");
        return request;
    }

//
//    /**
//     * Creates a search result for the key "1"
//     * @return
//     */
    private SearchResultEventWithSearchRequestKey[] createResults() {
        SearchResultEventWithSearchRequestKey[] results = new SearchResultEventWithSearchRequestKey[1];

        results[0] = new SearchResultEventWithSearchRequestKey(SearchResultsEvent.EMPTY_FAILED_FREQUENTLY_RELATED_SEARCH_RESULTS,
               new SipHashSearchRequestLookupKey("1"));

        return results;
    }
//


}
