/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.searching.requestprocessing;

import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.api.searching.lookup.SipHashSearchRequestLookupKey;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import javax.servlet.*;

import java.util.List;

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
        SearchResponseContext[] context = createHolder();
        SearchResponseContext[] context2 = createHolder();
        SearchResponseContext[] context3 = createHolder();

        map.addContext(key123,context);
        map.addContext(key1234,context2);
        map.addContext(key1234,context3);

        List<SearchResponseContext> contexts = map.removeContexts(key123);
        assertEquals(1,contexts.size());
        assertSame(contexts.get(0),context[0]);

        contexts = map.removeContexts(key123);
        assertEquals(0,contexts.size());

        contexts = map.removeContexts(key1234);
        assertEquals(2,contexts.size());
        assertSame(contexts.get(0),context2[0]);
        assertSame(contexts.get(1),context3[0]);

    }

    @Test
    public void testRemoveNonExistentContext() {
        MultiMapSearchResponseContextLookup map = new MultiMapSearchResponseContextLookup(new SystemPropertiesConfiguration());
        SearchRequestLookupKey key123 = new SipHashSearchRequestLookupKey("123");

        List<SearchResponseContext> contexts = map.removeContexts(key123);
        assertEquals(0,contexts.size());
    }

    private SearchResponseContext[] createHolder() {
        SearchResponseContext context = new AsyncServletSearchResponseContext(new StubAsyncCntext());
        return new SearchResponseContext[]{context};

    }

    @Test
    public void testAddAndRemoveContextForSingleItems() throws Exception {
        MultiMapSearchResponseContextLookup map = new MultiMapSearchResponseContextLookup(new SystemPropertiesConfiguration());

        SearchRequestLookupKey key123 = new SipHashSearchRequestLookupKey("123");
        SearchRequestLookupKey key1234 = new SipHashSearchRequestLookupKey("1234");
        SearchResponseContext[] context = createHolder();
        SearchResponseContext[] context2 = createHolder();

        map.addContext(key123,context);
        map.addContext(key1234,context2);

        List<SearchResponseContext> contexts = map.removeContexts(key123);
        assertEquals(1,contexts.size());
        assertSame(contexts.get(0),context[0]);

        contexts = map.removeContexts(key123);
        assertEquals(0,contexts.size());

        contexts = map.removeContexts(key1234);
        assertSame(contexts.get(0),context2[0]);
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
