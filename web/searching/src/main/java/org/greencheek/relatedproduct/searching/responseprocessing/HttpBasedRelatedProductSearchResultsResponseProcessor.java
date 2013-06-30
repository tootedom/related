package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsOutcomeType;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverterFactory;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */

public class HttpBasedRelatedProductSearchResultsResponseProcessor implements RelatedProductSearchResultsResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(HttpBasedRelatedProductSearchResultsResponseProcessor.class);

    private final SearchResultsConverterFactory converterLookup;
    private final Configuration configuration;

    public HttpBasedRelatedProductSearchResultsResponseProcessor(Configuration configuration,
                                                                 SearchResultsConverterFactory factory) {
        this.converterLookup = factory;
        this.configuration = configuration;
    }

    @Override
    public void processSearchResults(AsyncContext[] context, SearchResultsEvent results) {

        SearchResultsConverter converter = converterLookup.getConverter(results.getSearchType());

        if(converter==null) {
            if(log.isWarnEnabled()) {
                log.warn("No factory available for converting search results of type : {}",results.getSearchType());
            }
            return;
        }

        if(context==null) {
            if(log.isWarnEnabled()) {
                String res = converter.convertToString(results);
                log.warn("No async responses waiting for search results : {}",res);
            }

            return;
        }

        String response = converter.convertToString(results);
        String contentType = converter.contentType();
        int statusCode = getStatusCode(results.getOutcomeType());
        log.debug("Sending search results to {} waiting requests",context.length);
        for(AsyncContext ctx : context) {
            HttpServletResponse r = null;
            try {
                ServletRequest request =  ctx.getRequest();
                if(request==null) { // tomcat returns null when a timeout has occurred
                    continue;
                }


                r = (HttpServletResponse)ctx.getResponse();
                r.setStatus(statusCode);
                r.setContentType(contentType);
                r.getWriter().write(response);
            } catch (IOException e) {
                r.setStatus(500);
            } catch (IllegalStateException e) {
                log.warn("Async Context not available",e);
            } finally {
                try {
                    ctx.complete();
                } catch (IllegalStateException e) {
                    log.warn("Async Context not available, unable to call complete.  Timeout more than likely occurred");
                }
            }
        }
    }

    private int getStatusCode(SearchResultsOutcomeType type) {
        switch (type) {
            case EMPTY_RESULTS: return configuration.getNoFoundSearchResultsStatusCode();
            case FAILED_REQUEST: return configuration.getFailedSearchRequestStatusCode();
            case REQUEST_TIMEOUT: return configuration.getTimedOutSearchRequestStatusCode();
            case HAS_RESULTS: return configuration.getFoundSearchResultsStatusCode();
            default : return configuration.getFoundSearchResultsStatusCode();
        }
    }

    @Override
    public void shutdown() {

    }
}
