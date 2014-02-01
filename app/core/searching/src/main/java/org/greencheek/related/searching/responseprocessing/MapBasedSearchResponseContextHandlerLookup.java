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

package org.greencheek.related.searching.responseprocessing;

import org.greencheek.related.searching.requestprocessing.LogDebuggingSearchResponseContext;
import org.greencheek.related.util.config.Configuration;

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

    public static Map<Class, SearchResponseContextHandler> createDefaultHandlerMap(SearchResponseContextHandler defaultHandler,
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
