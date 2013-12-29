package org.greencheek.relatedproduct.searching.requestprocessing;

import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import javax.servlet.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests adding AsyncContext objects to the multi map.
 */
public class MultiMapAsyncContextLookupTest {

    @Test
    public void testRemoveContexts() throws Exception {
        MultiMapSearchResponseContextLookup map = new MultiMapSearchResponseContextLookup(new SystemPropertiesConfiguration());

        SearchRequestLookupKey key123 = new SipHashSearchRequestLookupKey("123");
        SearchRequestLookupKey key1234 = new SipHashSearchRequestLookupKey("1234");
        SearchResponseContextHolder context = createHolder();
        SearchResponseContextHolder context2 = createHolder();
        SearchResponseContextHolder context3 = createHolder();

        map.addContext(key123,context);
        map.addContext(key1234,context2);
        map.addContext(key1234,context3);

        SearchResponseContextHolder[] contexts = map.removeContexts(key123);
        assertEquals(1,contexts.length);
        assertSame(contexts[0],context);

        contexts = map.removeContexts(key123);
        assertEquals(0,contexts.length);

        contexts = map.removeContexts(key1234);
        assertEquals(2,contexts.length);
        assertSame(contexts[0],context2);
        assertSame(contexts[1],context3);

    }

    @Test
    public void testRemoveNonExistentContext() {
        MultiMapSearchResponseContextLookup map = new MultiMapSearchResponseContextLookup(new SystemPropertiesConfiguration());
        SearchRequestLookupKey key123 = new SipHashSearchRequestLookupKey("123");

        SearchResponseContextHolder[] contexts = map.removeContexts(key123);
        assertEquals(0,contexts.length);
    }

    private SearchResponseContextHolder createHolder() {
        SearchResponseContextHolder holder1 = new SearchResponseContextHolder();
        SearchResponseContext context = new AsyncServletSearchResponseContext(new StubAsyncCntext());
        holder1.setContexts(new SearchResponseContext[]{context});
        return holder1;
    }

    @Test
    public void testAddAndRemoveContextForSingleItems() throws Exception {
        MultiMapSearchResponseContextLookup map = new MultiMapSearchResponseContextLookup(new SystemPropertiesConfiguration());

        SearchRequestLookupKey key123 = new SipHashSearchRequestLookupKey("123");
        SearchRequestLookupKey key1234 = new SipHashSearchRequestLookupKey("1234");
        SearchResponseContextHolder context = createHolder();
        SearchResponseContextHolder context2 = createHolder();

        map.addContext(key123,context);
        map.addContext(key1234,context2);

        SearchResponseContextHolder[] contexts = map.removeContexts(key123);
        assertEquals(1,contexts.length);
        assertSame(contexts[0],context);

        contexts = map.removeContexts(key123);
        assertEquals(0,contexts.length);

        contexts = map.removeContexts(key1234);
        assertSame(contexts[0],context2);
    }


    private static class StubAsyncCntext implements AsyncContext {

        @Override
        public ServletRequest getRequest() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public ServletResponse getResponse() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean hasOriginalRequestAndResponse() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void dispatch() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void dispatch(String s) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void dispatch(ServletContext servletContext, String s) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void complete() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void start(Runnable runnable) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void addListener(AsyncListener asyncListener) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void addListener(AsyncListener asyncListener, ServletRequest servletRequest, ServletResponse servletResponse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public <T extends AsyncListener> T createListener(Class<T> tClass) throws ServletException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void setTimeout(long l) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public long getTimeout() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
