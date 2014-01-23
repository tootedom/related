package org.greencheek.related.searching.disruptor.responseprocessing;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.AsyncServletSearchResponseContext;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.searching.responseprocessing.MapBasedSearchResponseContextHandlerLookup;
import org.greencheek.related.searching.responseprocessing.SearchResponseContextHandler;
import org.greencheek.related.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.related.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.AsyncContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by dominictootell on 04/01/2014.
 */
public class ResponseContextTypeBasedResponseEventHandlerTest {

    ResponseEventHandler eventHandler;
    Configuration configuration;
    @Before
    public void setUp() {
        configuration = new SystemPropertiesConfiguration();
    }


    @After
    public void tearDown() {
        if(eventHandler!=null) {
            eventHandler.shutdown();
        }
    }

    private final ResponseEventHandler createResponseEventHandler(Configuration configuration,
                                                                  final SearchResponseContextHandlerLookup responseContextHandler,
                                                                  final SearchResultsConverterFactory factory,
                                                                  final CountDownLatch latch) {
        return new DisruptorBasedResponseContextTypeBasedResponseEventHandler(configuration,new ResponseEventHandler() {
            ResponseEventHandler delegate = new ResponseContextTypeBasedResponseEventHandler(responseContextHandler,factory);
            @Override
            public void handleResponseEvents(SearchResultsEvent[] searchResults, List<List<SearchResponseContext>> responseContexts) {
                delegate.handleResponseEvents(searchResults,responseContexts);
                latch.countDown();
            }

            @Override
            public void shutdown() {

            }
        });
    }


    private SearchResultsEvent createFrequentlyRelatedSearchResultResponse(String[] id, long[] frequency) {

        FrequentlyRelatedSearchResult[] results = new FrequentlyRelatedSearchResult[id.length];
        for(int i =id.length-1;i!=-1;i--) {
            results[i] = new FrequentlyRelatedSearchResult(id[i],frequency[i]);
        }

        return new SearchResultsEvent(SearchResultsOutcome.HAS_RESULTS,results);
    }

    private SearchResultsEvent createStringResponse() {
        return new SearchResultsEvent(SearchResultsOutcome.HAS_RESULTS,new String("s"));
    }

    private static class CountingSearchResponseContextHandler implements SearchResponseContextHandler{

        private AtomicInteger methodInvocationCount = new AtomicInteger(0);
        private volatile String resultsString;

        public int getMethodInvocationCount() {
            return methodInvocationCount.get();
        }

        public String getResultsString() {
            return resultsString;
        }

        @Override
        public void sendResults(String resultsAsString, String mediaType, SearchResultsEvent results, SearchResponseContext sctx) {
            this.resultsString = resultsAsString;
            methodInvocationCount.incrementAndGet();
        }
    }

    @Test
    public void testHandleFrequentlyRelatedSearchResultResponseEvent() throws Exception {
        SearchResultsConverterFactory resultsConverterFactory =  mock(SearchResultsConverterFactory.class);
        NumberOfSearchResultsConverter searchResultsConverter = new NumberOfSearchResultsConverter("application/json");


        when(resultsConverterFactory.getConverter(FrequentlyRelatedSearchResult[].class)).thenReturn(searchResultsConverter);



        CountingSearchResponseContextHandler contextHandler = new CountingSearchResponseContextHandler();
        CountingSearchResponseContextHandler defaultHandler = new CountingSearchResponseContextHandler();
        Map<Class,SearchResponseContextHandler> mappings = new HashMap<Class,SearchResponseContextHandler>(4);
        mappings.put(AsyncContext.class,contextHandler);

        CountDownLatch latch = new CountDownLatch(1);

        eventHandler = createResponseEventHandler(configuration,new MapBasedSearchResponseContextHandlerLookup(defaultHandler,mappings),resultsConverterFactory,latch);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        List<SearchResponseContext> holder = new ArrayList<SearchResponseContext>(1);
        holder.add(context);


        SearchResultsEvent searchResultsEvent = createFrequentlyRelatedSearchResultResponse(new String[]{"1", "2", "3"}, new long[]{1, 2, 3});

        List<List<SearchResponseContext>> contexts = new ArrayList<List<SearchResponseContext>>(1);
        contexts.add(holder);

        eventHandler.handleResponseEvents(new SearchResultsEvent[]{searchResultsEvent},contexts);
//        eventHandler.onEvent(frequentlyRelatedSearchResultsResponse, 2, true);

        // Tests that FrequentlyRelatedSearchResult[] search results are handled with the NumberOfSearchResultsConverter, and that the
        // AsyncContext contextHandler is called

        try {
            boolean handled = latch.await(2000, TimeUnit.MILLISECONDS);
            if(handled==false) fail("Failed waiting for latch in testHandleFrequentlyRelatedSearchResultResponseEvent");
        } catch(Exception e) {
            fail("Failed waiting for latch");
        }
        assertEquals(1, contextHandler.getMethodInvocationCount());
        assertEquals(searchResultsConverter.convertToString(searchResultsEvent),contextHandler.getResultsString());
        assertEquals(0,defaultHandler.getMethodInvocationCount());

        assertEquals(2, searchResultsConverter.getNoOfExecutions());

        verify(context, times(1)).close();
    }



    @Test
    public void testHandlingSearchResultsEventWithNoConverterResponseEvent() throws Exception {
        SearchResultsConverterFactory resultsConverterFactory =  mock(SearchResultsConverterFactory.class);
        NumberOfSearchResultsConverter searchResultsConverter = new NumberOfSearchResultsConverter("application/json");


        when(resultsConverterFactory.getConverter(FrequentlyRelatedSearchResult[].class)).thenReturn(searchResultsConverter);


        SearchResponseContextHandler contextHandler = mock(SearchResponseContextHandler.class);
        SearchResponseContextHandler defaultHandler = mock(SearchResponseContextHandler.class);
        Map<Class,SearchResponseContextHandler> mappings = new HashMap<Class,SearchResponseContextHandler>(4);
        mappings.put(AsyncContext.class,contextHandler);

        CountDownLatch latch = new CountDownLatch(1);
        eventHandler = createResponseEventHandler(configuration,new MapBasedSearchResponseContextHandlerLookup(defaultHandler,mappings),resultsConverterFactory,latch);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        List<SearchResponseContext> holder = new ArrayList<SearchResponseContext>(1);
        holder.add(context);

//        ResponseEvent frequentlyRelatedSearchResultsResponse = createStringResponse(holder);

        SearchResultsEvent searchResultsEvent =  createStringResponse();
        List<List<SearchResponseContext>> list= new ArrayList<List<SearchResponseContext>>(1);
        list.add(holder);
        eventHandler.handleResponseEvents(new SearchResultsEvent[] {searchResultsEvent},list);

        try {
            boolean handled = latch.await(2000, TimeUnit.MILLISECONDS);
            if(handled==false) fail("Failed waiting for latch");
        } catch(Exception e) {
            fail("Failed waiting for latch");
        }
        // Tests that FrequentlyRelatedSearchResult[] search results are handled with the NumberOfSearchResultsConverter, and that the
        // AsyncContext contextHandler is called
        assertEquals(0, searchResultsConverter.getNoOfExecutions());


        verify(contextHandler,times(1)).sendResults(eq(ResponseContextTypeBasedResponseEventHandler.ERROR_RESPONSE),eq(ResponseContextTypeBasedResponseEventHandler.ERROR_MEDIA_TYPE),any(SearchResultsEvent.class),any(SearchResponseContext.class));
        verify(defaultHandler,times(0)).sendResults(anyString(),anyString(),any(SearchResultsEvent.class),any(SearchResponseContext.class));


        verify(context, times(1)).close();
    }

    @Test
    public void testContextIsClosedWhenNoHandlerIsAvailable() {
        SearchResultsConverterFactory resultsConverterFactory =  mock(SearchResultsConverterFactory.class);
        NumberOfSearchResultsConverter searchResultsConverter = new NumberOfSearchResultsConverter("application/json");


        when(resultsConverterFactory.getConverter(FrequentlyRelatedSearchResult[].class)).thenReturn(searchResultsConverter);

        SearchResponseContextHandlerLookup handlerLookup = mock(SearchResponseContextHandlerLookup.class);
        when(handlerLookup.getHandler(any(Class.class))).thenReturn(null);
        CountDownLatch latch = new CountDownLatch(1);

        eventHandler = createResponseEventHandler(configuration,handlerLookup,resultsConverterFactory,latch);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        List<SearchResponseContext> holder = new ArrayList<SearchResponseContext>(1);
        holder.add(context);
        List<List<SearchResponseContext>> list= new ArrayList<List<SearchResponseContext>>(1);
        list.add(holder);
        eventHandler.handleResponseEvents(new SearchResultsEvent[]{createStringResponse()},list);

        try {
            boolean handled = latch.await(2000, TimeUnit.MILLISECONDS);
            if(handled==false) fail("Failed waiting for latch");
        } catch(Exception e) {
            fail("Failed waiting for latch");
        }
        // Tests that FrequentlyRelatedSearchResult[] search results are handled with the NumberOfSearchResultsConverter, and that the
        // AsyncContext contextHandler is called
        assertEquals(0, searchResultsConverter.getNoOfExecutions());


        verify(context, times(1)).close();


    }

    @Test
    public void testHandlingSearchResultsEventWithNoAwaitingContext() throws Exception {
        SearchResultsConverterFactory resultsConverterFactory =  mock(SearchResultsConverterFactory.class);
        NumberOfSearchResultsConverter searchResultsConverter = new NumberOfSearchResultsConverter("application/json");


        when(resultsConverterFactory.getConverter(FrequentlyRelatedSearchResult[].class)).thenReturn(searchResultsConverter);


        SearchResponseContextHandler contextHandler = mock(SearchResponseContextHandler.class);
        SearchResponseContextHandler defaultHandler = mock(SearchResponseContextHandler.class);
        Map<Class,SearchResponseContextHandler> mappings = new HashMap<Class,SearchResponseContextHandler>(4);
        mappings.put(AsyncContext.class,contextHandler);

        CountDownLatch latch = new CountDownLatch(1);

        eventHandler = createResponseEventHandler(configuration,new MapBasedSearchResponseContextHandlerLookup(defaultHandler,mappings),resultsConverterFactory,latch);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        List<SearchResponseContext> holder = new ArrayList<SearchResponseContext>(0);
        List<List<SearchResponseContext>> list= new ArrayList<List<SearchResponseContext>>(1);
        list.add(holder);


        eventHandler.handleResponseEvents(new SearchResultsEvent[]{createStringResponse()},list);

        // Tests that FrequentlyRelatedSearchResult[] search results are handled with the NumberOfSearchResultsConverter, and that the
        // AsyncContext contextHandler is called
        assertEquals(0, searchResultsConverter.getNoOfExecutions());

        try {
            boolean handled = latch.await(2000, TimeUnit.MILLISECONDS);
            if(handled==false) fail("Failed waiting for latch");
        } catch(Exception e) {
            fail("Failed waiting for latch");
        }

        verify(contextHandler,times(0)).sendResults(eq(ResponseContextTypeBasedResponseEventHandler.ERROR_RESPONSE),eq(ResponseContextTypeBasedResponseEventHandler.ERROR_MEDIA_TYPE),any(SearchResultsEvent.class),any(SearchResponseContext.class));
        verify(defaultHandler,times(0)).sendResults(anyString(),anyString(),any(SearchResultsEvent.class),any(SearchResponseContext.class));


        reset(contextHandler,defaultHandler);


        eventHandler.handleResponseEvents(new SearchResultsEvent[]{createStringResponse()},list);

        try {
            boolean handled = latch.await(2000, TimeUnit.MILLISECONDS);
            if(handled==false) fail("Failed waiting for latch in");
        } catch(Exception e) {
            fail("Failed waiting for latch");
        }
        assertEquals(0, searchResultsConverter.getNoOfExecutions());

        verify(contextHandler,times(0)).sendResults(eq(ResponseContextTypeBasedResponseEventHandler.ERROR_RESPONSE),eq(ResponseContextTypeBasedResponseEventHandler.ERROR_MEDIA_TYPE),any(SearchResultsEvent.class),any(SearchResponseContext.class));
        verify(defaultHandler,times(0)).sendResults(anyString(),anyString(),any(SearchResultsEvent.class),any(SearchResponseContext.class));


    }


//    @Override
//    public void handleResponseEvent(ResponseEvent event) {
//        try {
//            SearchResultsEvent results = event.getResults();
//            SearchResultsConverter converter = converterLookup.getConverter(results.getSearchResultsType());
//
//            if (converter == null) {
//                if (log.isWarnEnabled()) {
//                    log.warn("No factory available for converting search results of type : {}", results.getSearchResultsType());
//                }
//            }
//
//            SearchResponseContextHolder[] awaitingResponses = event.getContexts();
//
//            if (awaitingResponses == null) {
//                if (log.isWarnEnabled() && converter!=null) {
//                    String res = converter.convertToString(results);
//                    log.warn("No async responses waiting for search results : {}", res);
//                }
//
//                return;
//            }
//
//            log.debug("Sending search results to {} waiting responses", awaitingResponses.length);
//
//            String response = "{}";
//            String mediaType = "application/json";
//
//            if(converter!=null) {
//                response = converter.convertToString(results);
//                mediaType = converter.contentType();
//            } else {
//                results = new SearchResultsEvent(SearchResultsOutcome.MISSING_SEARCH_RESULTS_HANDLER,results.getSearchResults());
//            }
//
//            for(SearchResponseContextHolder contextHolder : awaitingResponses) {
//                SearchResponseContext[] responseContexts = contextHolder.getContexts();
//                log.debug("Sending search results to {} pending response listeners", responseContexts.length);
//
//                for (SearchResponseContext sctx : responseContexts) {
//                    try {
//                        SearchResponseContextHandler handler = contextHandlerLookup.getHandler(sctx.getContextType());
//
//                        if(handler==null) {
//                            log.error("No response handler defined for waiting response type: {}",sctx.getContextType());
//                        } else {
//                            handler.sendResults(response, mediaType, results, sctx);
//                        }
//                    } finally {
//                        sctx.close();
//                    }
//
//                }
//            }
//        } finally {
//            event.setContexts(null);
//            event.setResults(null);
//        }
//    }


    private class NumberOfSearchResultsConverter implements SearchResultsConverter<FrequentlyRelatedSearchResult[]> {

        private final String mediaType;
        private final AtomicInteger executions = new AtomicInteger(0);

        public NumberOfSearchResultsConverter(String mediaType) {
            this.mediaType = mediaType;
        }

        @Override
        public String contentType() {
            return mediaType;
        }

        public int getNoOfExecutions() {
            return executions.get();
        }

        @Override
        public String convertToString(SearchResultsEvent<FrequentlyRelatedSearchResult[]> results) {
            executions.incrementAndGet();
            return "{ \"num\": \""+Integer.toString(results.getSearchResults().length) +"\" }";
        }
    }
}
