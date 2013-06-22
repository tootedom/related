package org.greencheek.relatedproduct.searching.domain.api;

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


    public SearchRequestEvent(AsyncContext context) {
        this.requestContext = context;
    }

    public AsyncContext getRequestContext() {
        return requestContext;
    }

}
