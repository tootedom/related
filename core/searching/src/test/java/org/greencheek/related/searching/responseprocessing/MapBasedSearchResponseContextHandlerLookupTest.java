package org.greencheek.related.searching.responseprocessing;

import org.greencheek.related.searching.requestprocessing.LogDebuggingSearchResponseContext;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by dominictootell on 03/01/2014.
 */
public class MapBasedSearchResponseContextHandlerLookupTest {

    private SearchResponseContextHandlerLookup defaultLookup;
    private SearchResponseContextHandlerLookup customMappingsLookup;

    private SearchResponseContextHandler mockHandler1;
    private SearchResponseContextHandler mockHandler2;


    @Before
    public void setUp() {
        Configuration config = new SystemPropertiesConfiguration();
        defaultLookup = new MapBasedSearchResponseContextHandlerLookup(config);

        Map<Class,SearchResponseContextHandler> mappings = new HashMap<>();

        mockHandler1 = mock(SearchResponseContextHandler.class);
        mockHandler2 = mock(SearchResponseContextHandler.class);

        mappings.put(Long.class,mockHandler1);
        mappings.put(String.class,mockHandler2);

        customMappingsLookup = new MapBasedSearchResponseContextHandlerLookup(DebugSearchResponseContextHandler.INSTANCE,mappings);
    }




    @Test
    public void testGetHandlerReturnsDefaultMapping() throws Exception {
        SearchResponseContextHandler h = defaultLookup.getHandler(Long.class);

        assertSame(DebugSearchResponseContextHandler.INSTANCE,h);
    }

    @Test
    public void testDefaultHandlerReturnedForCustomLookup() {
        assertSame(mockHandler1, customMappingsLookup.getHandler(Long.class));
        assertSame(mockHandler2,customMappingsLookup.getHandler(String.class));

        assertSame(DebugSearchResponseContextHandler.INSTANCE,customMappingsLookup.getHandler(AsyncContext.class));
    }

    @Test
    public void testDefaultMappings() {
        assertTrue(defaultLookup.getHandler(AsyncContext.class) instanceof SearchResponseContextHandler);
        assertSame(DebugSearchResponseContextHandler.INSTANCE,defaultLookup.getHandler(LogDebuggingSearchResponseContext.class));
    }
}
