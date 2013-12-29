package org.greencheek.relatedproduct.searching.domain.api;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequestEvent {
    public SearchResponseContextHolder requestContext = null;
    public RelatedProductSearch searchRequest = null;
    public RelatedProductSearchExecutor searchExecutor = null;

    public SearchRequestEvent() {

    }

    public void populateSearchRequestEvent(SearchResponseContextHolder context, RelatedProductSearch searchRequest,RelatedProductSearchExecutor searchExecutor) {
        this.requestContext = context;
        this.searchRequest = searchRequest;
        this.searchExecutor = searchExecutor;
    }

    public RelatedProductSearchExecutor getSearchExecutor() {
        return searchExecutor;
    }

    public SearchResponseContextHolder getRequestContext() {
        return requestContext;
    }

    public RelatedProductSearch getSearchRequest() {
        return this.searchRequest;
    }

}
