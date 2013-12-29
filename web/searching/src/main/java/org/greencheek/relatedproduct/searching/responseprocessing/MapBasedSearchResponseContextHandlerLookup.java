package org.greencheek.relatedproduct.searching.responseprocessing;

import org.greencheek.relatedproduct.searching.requestprocessing.LogDebuggingSearchResponseContext;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses a simple hash map, to map Class objects to associated handlers.
 */
public class MapBasedSearchResponseContextHandlerLookup implements SearchResponseContextHandlerLookup {

    private final SearchResponseContextHandler defaultMapping;
    private final Map<Class,SearchResponseContextHandler> mappings;

    public MapBasedSearchResponseContextHandlerLookup(final Configuration config) {

        this(DebugSearchResponseContextHandler.INSTANCE,createDefaultHandlerMap(DebugSearchResponseContextHandler.INSTANCE,config));
    }

    public MapBasedSearchResponseContextHandlerLookup(SearchResponseContextHandler defaultMapping,
                                                      Map<Class,SearchResponseContextHandler> mappings) {
        this.defaultMapping = defaultMapping;
        this.mappings=mappings;

    }

    private static Map<Class, SearchResponseContextHandler> createDefaultHandlerMap(SearchResponseContextHandler defaultHandler,
                                                                                    Configuration config) {
        Map<Class,SearchResponseContextHandler> mappings = new HashMap<Class,SearchResponseContextHandler>(4);
        mappings.put(AsyncContext.class,new HttpAsyncSearchResponseContextHandler(config));
        mappings.put(LogDebuggingSearchResponseContext.class,defaultHandler);
        return mappings;
    }

    @Override
    public SearchResponseContextHandler getHandler(Class responseClassToHandle) {
        SearchResponseContextHandler handler = mappings.get(responseClassToHandle);
        return handler == null ? defaultMapping : handler;
    }
}
