package org.greencheek.relatedproduct.searching.disruptor.responseprocessing;

import org.greencheek.relatedproduct.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.relatedproduct.api.searching.SearchResultsOutcome;
import org.greencheek.relatedproduct.searching.domain.api.ResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.AsyncServletSearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.searching.responseprocessing.MapBasedSearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandler;
import org.greencheek.relatedproduct.searching.responseprocessing.SearchResponseContextHandlerLookup;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.junit.Test;

import javax.servlet.AsyncContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by dominictootell on 04/01/2014.
 */
public class ResponseContextTypeBasedResponseEventHandlerTest {



    private final ResponseContextTypeBasedResponseEventHandler createResponseEventHandler(SearchResponseContextHandlerLookup responseContextHandler,
                                                                                SearchResultsConverterFactory factory) {
        return new ResponseContextTypeBasedResponseEventHandler(responseContextHandler,factory);
    }


    private SearchResultsEvent createFrequentlyRelatedSearchResultResponse(String[] id, long[] frequency, SearchResponseContextHolder... holders) {

        FrequentlyRelatedSearchResult[] results = new FrequentlyRelatedSearchResult[id.length];
        for(int i =id.length-1;i!=-1;i--) {
            results[i] = new FrequentlyRelatedSearchResult(id[i],frequency[i]);
        }

        return new SearchResultsEvent(SearchResultsOutcome.HAS_RESULTS,results);
    }

    private SearchResultsEvent createStringResponse(SearchResponseContextHolder... holders) {
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


        ResponseContextTypeBasedResponseEventHandler eventHandler = createResponseEventHandler(new MapBasedSearchResponseContextHandlerLookup(defaultHandler,mappings),resultsConverterFactory);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        SearchResponseContextHolder holder = new SearchResponseContextHolder();
        holder.setContexts(context);

        SearchResultsEvent searchResultsEvent = createFrequentlyRelatedSearchResultResponse(new String[]{"1", "2", "3"}, new long[]{1, 2, 3}, holder);

        SearchResponseContextHolder[][] contexts = new SearchResponseContextHolder[1][1];
        contexts[0] = new SearchResponseContextHolder[]{holder};

        eventHandler.handleResponseEvents(new SearchResultsEvent[]{searchResultsEvent},contexts);
//        eventHandler.onEvent(frequentlyRelatedSearchResultsResponse, 2, true);

        // Tests that FrequentlyRelatedSearchResult[] search results are handled with the NumberOfSearchResultsConverter, and that the
        // AsyncContext contextHandler is called


        assertEquals(1,contextHandler.getMethodInvocationCount());
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


        ResponseContextTypeBasedResponseEventHandler eventHandler = createResponseEventHandler(new MapBasedSearchResponseContextHandlerLookup(defaultHandler,mappings),resultsConverterFactory);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        SearchResponseContextHolder holder = new SearchResponseContextHolder();
        holder.setContexts(context);

//        ResponseEvent frequentlyRelatedSearchResultsResponse = createStringResponse(holder);

        SearchResultsEvent searchResultsEvent =  createStringResponse(holder);
        eventHandler.handleResponseEvent(searchResultsEvent,new SearchResponseContextHolder[]{holder});

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
        ResponseContextTypeBasedResponseEventHandler eventHandler = createResponseEventHandler(handlerLookup,resultsConverterFactory);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        SearchResponseContextHolder holder = new SearchResponseContextHolder();
        holder.setContexts(context);

        eventHandler.handleResponseEvent(createStringResponse(holder),new SearchResponseContextHolder[]{holder});

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


        ResponseContextTypeBasedResponseEventHandler eventHandler = createResponseEventHandler(new MapBasedSearchResponseContextHandlerLookup(defaultHandler,mappings),resultsConverterFactory);

        SearchResponseContext<AsyncContext> context = mock(AsyncServletSearchResponseContext.class);
        when(context.getContextType()).thenReturn(AsyncContext.class);
        SearchResponseContextHolder holder = new SearchResponseContextHolder();
        holder.setContexts(null);

        eventHandler.handleResponseEvent(createStringResponse(null),new SearchResponseContextHolder[]{holder});

        // Tests that FrequentlyRelatedSearchResult[] search results are handled with the NumberOfSearchResultsConverter, and that the
        // AsyncContext contextHandler is called
        assertEquals(0, searchResultsConverter.getNoOfExecutions());


        verify(contextHandler,times(0)).sendResults(eq(ResponseContextTypeBasedResponseEventHandler.ERROR_RESPONSE),eq(ResponseContextTypeBasedResponseEventHandler.ERROR_MEDIA_TYPE),any(SearchResultsEvent.class),any(SearchResponseContext.class));
        verify(defaultHandler,times(0)).sendResults(anyString(),anyString(),any(SearchResultsEvent.class),any(SearchResponseContext.class));


        reset(contextHandler,defaultHandler);


        eventHandler.handleResponseEvent(createStringResponse(new SearchResponseContextHolder[0]),new SearchResponseContextHolder[]{holder});

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
