package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  A SearchResponseContextHandler that sends results to Servlet3 AsyncContext, and the awaiting Servlet Response object
 *
 */

public class HttpAsyncSearchResponseContextHandler implements SearchResponseContextHandler<AsyncContext> {
    private static final Logger log = LoggerFactory.getLogger(HttpAsyncSearchResponseContextHandler.class);

    private final Configuration configuration;

    public HttpAsyncSearchResponseContextHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void sendResults(String resultsAsString, String mediaType, SearchResultsEvent results, SearchResponseContext<AsyncContext> sctx) {
        AsyncContext ctx  = sctx.getSearchResponseContext();
        HttpServletResponse r = null;
        try {
            ServletRequest request =  ctx.getRequest();
            if(request==null) { // tomcat returns null when a timeout has occurred
                return;
            }

            r = (HttpServletResponse)ctx.getResponse();
            if(r!=null) {
                int statusCode = configuration.getResponseCode(results.getOutcomeType());
                r.setStatus(statusCode);
                r.setContentType(mediaType);
                r.getWriter().write(resultsAsString);
            }
        } catch (IOException e) {
            if(r!=null) {
                r.setStatus(500);
            }
        } catch (IllegalStateException e) {
            log.warn("Async Context not available",e);
        } finally {
            try {
                ctx.complete();
            } catch (IllegalStateException e) {
                log.warn("Async Context not available, unable to call complete.  Timeout more than likely occurred",e);
            }
        }
    }
}
