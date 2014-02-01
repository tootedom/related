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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedRelatedContentSearchRequestProcessorHandler implements RelatedContentSearchRequestProcessorHandler {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedContentSearchRequestProcessorHandler.class);

    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private final RelatedItemSearchExecutor searchRequestExecutor;
    private final RelatedItemSearchResultsToResponseGateway contextStorage;

    public DisruptorBasedRelatedContentSearchRequestProcessorHandler(RelatedItemSearchResultsToResponseGateway contextStorage,
                                                                     RelatedItemSearchExecutor searchExecutor) {
        this.searchRequestExecutor = searchExecutor;
        this.contextStorage = contextStorage;
    }

    @Override
    public void onEvent(RelatedItemSearchRequest event, long sequence, boolean endOfBatch) throws Exception {
        handleRequest(event,searchRequestExecutor);
    }

    public void handleRequest(RelatedItemSearchRequest searchRequest, RelatedItemSearchExecutor searchExecutor) {
        contextStorage.storeResponseContextForSearchRequest(searchRequest.getSearchRequest().getLookupKey(), searchRequest.getRequestContexts());
        searchRequestExecutor.executeSearch(searchRequest.getSearchRequest());
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

            try {
                log.info("Shutting down search request executor");
                searchRequestExecutor.shutdown();
            } catch(Exception e) {
                log.warn("Problem shutting down request executor");
            }
        }
    }

}
