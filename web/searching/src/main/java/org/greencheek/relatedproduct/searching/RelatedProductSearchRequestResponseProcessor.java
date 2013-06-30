package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.domain.searching.SearchResult;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.responseprocessing.resultsconverter.SearchResultsConverter;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductSearchRequestResponseProcessor {

    public void handleRequest(SearchRequestLookupKey requestKey, AsyncContext requestCtx);

    public void handleResponse(SearchRequestLookupKey requestKey, SearchResultsEvent results);


    public void shutdown();
}
