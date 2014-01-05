package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 *
 */
@Deprecated
public class DisruptorBasedSearchEventHandlerTest {

//
//
//    private Configuration configuration = new SystemPropertiesConfiguration();
//    private SearchResponseContextLookup lookup = new MultiMapSearchResponseContextLookup(configuration);
//    private TestRelatedProductSearchResultsResponseProcessor responseProcessor = new TestRelatedProductSearchResultsResponseProcessor();
//    private SearchEventHandler handler = new DisruptorBasedSearchEventHandler(configuration,lookup,responseProcessor);
//    private TestRelatedProductSearchExecutor searchExecutor = new TestRelatedProductSearchExecutor();
//
//    private SearchResponseEvent getSearchRequest(RelatedProductSearchExecutor executor) {
//        SearchResponseEvent event = new SearchResponseEvent();
////        event.setEventType(SearchEventType.SEARCH_REQUEST);
//        event.setRequestKeyReference(new SipHashSearchRequestLookupKey("1"));
////        event.getSearchRequestEvent().populateSearchRequestEvent(new SearchResponseContextHolder(),mock(RelatedProductSearch.class),executor);
//        return event;
//    }
//
//    private SearchResponseEvent getSearchResult(RelatedProductSearchExecutor executor) {
//        SearchResponseEvent event = new SearchResponseEvent();
////        event.setEventType(SearchEventType.SEARCH_RESULT);
//        event.setRequestKeyReference(new SipHashSearchRequestLookupKey("1"));
////        event.getSearchRequestEvent().populateSearchRequestEvent(new SearchResponseContextHolder(),mock(RelatedProductSearch.class),executor);
//        return event;
//    }
//
//    @Test
//    public void testSearchExecutorGetsCalledForSearchRequest() {
//        try {
//            handler.onEvent(getSearchRequest(searchExecutor),1,true);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        assertEquals(0,responseProcessor.getNoOfCalls());
//        assertEquals(1,searchExecutor.getNoOfCalls());
//
//    }
//
//    @Test
//    public void testReponseProcessorGetsCalledForSearchRespose() {
//        try {
//            handler.onEvent(getSearchResult(searchExecutor),1,true);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        assertEquals(1,responseProcessor.getNoOfCalls());
//        assertEquals(0,searchExecutor.getNoOfCalls());
//
//    }
//
//    @Test
//    public void testSearchExecutorGetsCalledOnceForSearchRequest() {
//        try {
//            handler.onEvent(getSearchRequest(searchExecutor),1,true);
//            handler.onEvent(getSearchRequest(searchExecutor),1,true);
//            handler.onEvent(getSearchRequest(searchExecutor),1,true);
//            handler.onEvent(getSearchRequest(searchExecutor),1,true);
//
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        assertEquals(0,responseProcessor.getNoOfCalls());
//        assertEquals(1,searchExecutor.getNoOfCalls());
//
//    }
//
//    @Test
//    public void testShutdown() {
//        handler.shutdown();
//
//        assertTrue(responseProcessor.isShutdown());
//        assertFalse(searchExecutor.isShutdown());
//
//    }
//
//    private class TestRelatedProductSearchExecutor implements RelatedProductSearchExecutor {
//
//        private AtomicInteger noOfCalls = new AtomicInteger(0);
//        private AtomicBoolean shutdown = new AtomicBoolean(false);
//        @Override
//        public void executeSearch(RelatedProductSearch searchRequest) {
//            noOfCalls.incrementAndGet();
//        }
//
//        public int getNoOfCalls() {
//            return noOfCalls.get();
//        }
//
//        @Override
//        public void shutdown() {
//            shutdown.set(true);
//        }
//
//        public boolean isShutdown() {
//            return shutdown.get();
//        }
//    }
//
//    private class TestRelatedProductSearchResultsResponseProcessor implements RelatedProductSearchResultsResponseProcessor
//    {
//        private AtomicInteger noOfCalls = new AtomicInteger(0);
//        private AtomicBoolean shutdown = new AtomicBoolean(false);
//
//
//        public int getNoOfCalls() {
//            return noOfCalls.get();
//        }
//
//        @Override
//        public void handleResponse(SearchResultEventWithSearchRequestKey[] results) {
//            noOfCalls.incrementAndGet();
//
//        }
//
//        @Override
//        public void shutdown() {
//            shutdown.set(true);
//        }
//
//        public boolean isShutdown() {
//            return shutdown.get();
//        }
//    }

}
