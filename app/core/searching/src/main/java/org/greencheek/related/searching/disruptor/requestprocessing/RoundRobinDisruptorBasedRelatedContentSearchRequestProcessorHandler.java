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

package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.util.arrayindexing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler{

    private static final Logger log = LoggerFactory.getLogger(RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler.class);


    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final RelatedItemSearchResultsToResponseGateway contextStorage;
    private final RelatedItemSearchExecutor[] searchRequestExecutor;

    private int currentIndex = 0;
    private final int mask;

    public RoundRobinDisruptorBasedRelatedContentSearchRequestProcessorHandler(RelatedItemSearchResultsToResponseGateway contextStorage,
                                                                               RelatedItemSearchExecutor[] searchExecutor) {
        check(searchExecutor);
        this.contextStorage = contextStorage;
        this.searchRequestExecutor = searchExecutor;

        mask = searchExecutor.length-1;
    }

    private void check(RelatedItemSearchExecutor[] searchExecutor) throws InstantiationError {
        int length = searchExecutor.length;

        if(length != Util.ceilingNextPowerOfTwo(length)) {
            throw new InstantiationError("The length of the context lookup and search executors array must be a power of 2");
        }

    }

    @Override
    public void onEvent(RelatedItemSearchRequest event, long sequence, boolean endOfBatch) throws Exception {
        handleRequest(event,searchRequestExecutor[this.currentIndex++ & mask]);
    }

    public void handleRequest(RelatedItemSearchRequest searchRequest, RelatedItemSearchExecutor searchExecutor) {
        contextStorage.storeResponseContextForSearchRequest(searchRequest.getSearchRequest().getLookupKey(), searchRequest.getRequestContexts());
        searchExecutor.executeSearch(searchRequest.getSearchRequest());
        searchRequest.setRequestContexts(null);
    }


    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {

            try {
                log.info("Shutting down response context gateway respository");
                contextStorage.shutdown();
            } catch(Exception e) {
                log.warn("Problem shutting down response context gateway respository");
            }

            for (RelatedItemSearchExecutor searchExecutor : searchRequestExecutor) {
                log.debug("Shutting Down Related Product Search Executor");
                try {
                    searchExecutor.shutdown();
                } catch(Exception e) {
                    log.warn("Unable to stop SearchRequestExecutor");
                }
            }
        }
    }

}
