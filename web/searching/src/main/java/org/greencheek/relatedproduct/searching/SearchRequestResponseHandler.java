package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.resultsconverter.SearchResultsConverter;

import javax.servlet.AsyncContext;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
public interface SearchRequestResponseHandler {

    public void handleRequest(SearchRequestLookupKey requestKey, AsyncContext requestCtx);

    public void handleResponse(SearchRequestLookupKey requestKey, SearchResultsConverter results);


    public void shutdown();
}
