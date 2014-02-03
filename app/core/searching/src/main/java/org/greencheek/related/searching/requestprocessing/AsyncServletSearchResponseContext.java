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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;

/**
 * stores a reference to a servlet 3 AsyncContext object.
 * This can be used to obtain the ServletResponse object for sending
 * the search result to the user.
 *
 */
public class AsyncServletSearchResponseContext implements SearchResponseContext<AsyncContext> {
    private static final Logger log = LoggerFactory.getLogger(AsyncServletSearchResponseContext.class);

    private static final Class<AsyncContext> contextType = AsyncContext.class;
    private final AsyncContext context;
    private final long creationTime;

    public AsyncServletSearchResponseContext(AsyncContext context) {
        this(context, System.nanoTime());
    }

    public AsyncServletSearchResponseContext(AsyncContext context, long creationTime) {
        this.context = context;
        this.creationTime = creationTime;
    }

    @Override
    public Class<AsyncContext> getContextType() {
        return contextType;
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
                log.warn("Unable to call complete on AsyncContext.  Timeout more than likely occurred",e);
            }
        }
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }
}
