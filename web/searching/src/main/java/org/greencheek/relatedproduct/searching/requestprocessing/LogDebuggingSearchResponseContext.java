package org.greencheek.relatedproduct.searching.requestprocessing;

/**
 * Respresents a static context that signals that the search response
 * should be debug logged {@link org.greencheek.relatedproduct.searching.responseprocessing.DebugSearchResponseContextHandler}
 *
 */
public class LogDebuggingSearchResponseContext implements SearchResponseContext<LogDebuggingSearchResponseContext> {

    public static final LogDebuggingSearchResponseContext INSTANCE = new LogDebuggingSearchResponseContext();

    private LogDebuggingSearchResponseContext() {}


    @Override
    public Class<LogDebuggingSearchResponseContext> getContextType() {
        return LogDebuggingSearchResponseContext.class;
    }

    @Override
    public LogDebuggingSearchResponseContext getSearchResponseContext() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public long getCreationTime() {
        return -1;
    }


}
