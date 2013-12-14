package org.greencheek.relatedproduct.searching.domain.api;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;

import javax.servlet.AsyncContext;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequestEvent {
    public final AsyncContext requestContext;
    public final RelatedProductSearch searchRequest;
    public final RelatedProductSearchExecutor searchExecutor;


    public SearchRequestEvent(AsyncContext context, RelatedProductSearch searchRequest) {
        this.requestContext = context;
        this.searchRequest = searchRequest;
        this.searchExecutor = null;
    }

    public SearchRequestEvent(AsyncContext context, RelatedProductSearch searchRequest,RelatedProductSearchExecutor searchExecutor) {
        this.requestContext = context;
        this.searchRequest = searchRequest;
        this.searchExecutor = searchExecutor;
    }

    public RelatedProductSearchExecutor getSearchExecutor() {
        return searchExecutor;
    }

    public AsyncContext getRequestContext() {
        return requestContext;
    }

    public RelatedProductSearch getSearchRequest() {
        return this.searchRequest;
    }

}
