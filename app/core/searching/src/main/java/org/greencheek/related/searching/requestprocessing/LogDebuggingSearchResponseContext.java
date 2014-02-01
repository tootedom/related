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

/**
 * Respresents a static context that signals that the search response
 * should be debug logged {@link org.greencheek.related.searching.responseprocessing.DebugSearchResponseContextHandler}
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
