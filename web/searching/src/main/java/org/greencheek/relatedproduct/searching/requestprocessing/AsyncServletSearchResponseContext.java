package org.greencheek.relatedproduct.searching.requestprocessing;

import javax.servlet.AsyncContext;

/**
 * stores a reference to a servlet 3 AsyncContext object.
 * This can be used to obtain the ServletResponse object for sending
 * the search result to the user.
 *
 */
public class AsyncServletSearchResponseContext implements SearchResponseContext<AsyncContext> {

    private final AsyncContext context;

    public AsyncServletSearchResponseContext(AsyncContext context) {
        this.context = context;
    }

    @Override
    public Class<AsyncContext> getContextType() {
        return AsyncContext.class;
    }

    @Override
    public AsyncContext getSearchResponseContext() {
        return context;
    }

    @Override
    public void close() {
        if(context!=null) {
            try {
                context.complete();
            } catch(Exception e) {
                //
            }
        }
    }
}
