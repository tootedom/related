package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.servlet.AsyncContext;
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
@Named
public class HttpBasedRelatedProductSearchResultsResponseProcessor implements RelatedProductSearchResultsResponseProcessor {
    private static final Logger log = LoggerFactory.getLogger(HttpBasedRelatedProductSearchResultsResponseProcessor.class);


    @Override
    public void processSearchResults(List<AsyncContext> context, SearchResultsConverter results) {
        if(context==null) {
            log.warn("No async responses waiting for search results : {}",results.convertToString());
            return;
        }

        String response = results.convertToString();
        String contentType = results.contentType();

        for(AsyncContext ctx : context) {
            HttpServletResponse r = (HttpServletResponse)ctx.getResponse();
            try {
                r.setStatus(200);
                r.setContentType(contentType);
                ctx.getResponse().getWriter().write(response);
            } catch (IOException e) {
                r.setStatus(500);
            } finally {
                ctx.complete();
            }
        }
    }
}
