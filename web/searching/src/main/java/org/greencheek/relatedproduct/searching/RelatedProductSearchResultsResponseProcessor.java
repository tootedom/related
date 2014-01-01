package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductSearchResultsResponseProcessor {
    public void handleResponse(SearchResultEventWithSearchRequestKey[] results);
//    public void processSearchResults(SearchResponseContextHolder[] context, SearchResultsEvent results);
    public void shutdown();
}
