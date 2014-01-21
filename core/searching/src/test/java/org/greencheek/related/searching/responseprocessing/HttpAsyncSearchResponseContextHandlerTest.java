package org.greencheek.related.searching.responseprocessing;

import org.greencheek.related.api.searching.FrequentlyRelatedSearchResult;
import org.greencheek.related.api.searching.SearchResultsOutcome;
import org.greencheek.related.searching.domain.api.SearchResultsEvent;
import org.greencheek.related.searching.requestprocessing.AsyncServletSearchResponseContext;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.util.config.Configuration;
import org.junit.Test;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by dominictootell on 03/01/2014.
 */
public class HttpAsyncSearchResponseContextHandlerTest {

    Configuration configuration;

    private SearchResponseContextHandler getHandler(Configuration config) {
        return new HttpAsyncSearchResponseContextHandler(config);
    }

    private AsyncContext getAsyncContext(ServletRequest request, ServletResponse response) {
        AsyncContext userResponse = mock(AsyncContext.class);
        when(userResponse.getRequest()).thenReturn(request);
        when(userResponse.getResponse()).thenReturn(response);
        return userResponse;
    }


    public SearchResultsEvent createSearchResultEvent() {
        return new SearchResultsEvent(SearchResultsOutcome.HAS_RESULTS,new FrequentlyRelatedSearchResult[]{mock(FrequentlyRelatedSearchResult.class)});
    }

    @Test
    public void testResultsSent() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getResponseCode(any(SearchResultsOutcome.class))).thenReturn(200);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        ServletOutputStream out = new ServletOutputStream() {


            public OutputStream getOutputStream() {
                return stream;
            }

            @Override
            public void write(int b) throws IOException {
                stream.write(b);
            }
        };

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        try {
            when(response.getOutputStream()).thenReturn(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


        AsyncContext userResponse = getAsyncContext(request,response);

        SearchResponseContext responseHolder = new AsyncServletSearchResponseContext(userResponse,System.nanoTime());

        SearchResponseContextHandler handler = getHandler(configuration);

        handler.sendResults("results","appplication/json",createSearchResultEvent(),responseHolder);

        String s = new String(stream.toByteArray());
        if(s.length()==0) {
            s = stringWriter.toString();
        }

        assertEquals("results",s);

        verify(userResponse,times(1)).complete();

    }


    @Test
    public void testResultsNotSentWhenRequestOrResponseNotAvailable() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getResponseCode(any(SearchResultsOutcome.class))).thenReturn(200);
        AsyncContext userResponse = getAsyncContext(null,null);

        SearchResponseContext responseHolder = new AsyncServletSearchResponseContext(userResponse,System.nanoTime());

        SearchResponseContextHandler handler = getHandler(configuration);

        handler.sendResults("results","appplication/json",null,responseHolder);

        verify(configuration,times(0)).getResponseCode(any(SearchResultsOutcome.class));

        verify(userResponse,times(1)).complete();
    }

    @Test
    public void testIOExceptionIsHandled() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getResponseCode(any(SearchResultsOutcome.class))).thenReturn(200);


        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);


        try {
            doThrow(new IOException()).when(response).getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AsyncContext userResponse = getAsyncContext(request,response);

        SearchResponseContext responseHolder = new AsyncServletSearchResponseContext(userResponse,System.nanoTime());

        SearchResponseContextHandler handler = getHandler(configuration);

        handler.sendResults("results","appplication/json",mock(SearchResultsEvent.class),responseHolder);


        verify(response,times(1)).setStatus(500);
        verify(userResponse, times(1)).complete();
    }


//
//    @Override
//    public void sendResults(String resultsAsString, String mediaType, SearchResultsEvent results, SearchResponseContext<AsyncContext> sctx) {
//        int statusCode = configuration.getResponseCode(results.getOutcomeType());
//        AsyncContext ctx  = sctx.getSearchResponseContext();
//        HttpServletResponse r = null;
//        try {
//            ServletRequest request =  ctx.getRequest();
//            if(request==null) { // tomcat returns null when a timeout has occurred
//                return;
//            }
//
//            r = (HttpServletResponse)ctx.getResponse();
//            r.setStatus(statusCode);
//            r.setContentType(mediaType);
//            r.getWriter().write(resultsAsString);
//        } catch (IOException e) {
//            r.setStatus(500);
//        } catch (IllegalStateException e) {
//            log.warn("Async Context not available",e);
//        } finally {
//            try {
//                ctx.complete();
//            } catch (IllegalStateException e) {
//                log.warn("Async Context not available, unable to call complete.  Timeout more than likely occurred",e);
//            }
//        }
//    }
}
