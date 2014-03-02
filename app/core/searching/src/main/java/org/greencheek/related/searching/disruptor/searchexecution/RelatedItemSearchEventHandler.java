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

package org.greencheek.related.searching.disruptor.searchexecution;

import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.related.searching.RelatedItemSearchRepository;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Event Handler that receives event from the ring buffer in the form of {@link org.greencheek.related.api.searching.RelatedItemSearch} objects.
 * This are the sent in batches to the {@link org.greencheek.related.searching.RelatedItemSearchRepository} that performs the users search,
 * returning the batch of search results.  The {@link org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway} is called
 * to deal with processing those results.
 */
public class RelatedItemSearchEventHandler implements RelatedItemSearchDisruptorEventHandler {


    private static final Logger log = LoggerFactory.getLogger(RelatedItemSearchEventHandler.class);

    private final Map<SearchRequestLookupKey, RelatedItemSearch> searchMap;
    private final RelatedItemSearchRepository searchRespository;
    private final RelatedItemSearchResultsToResponseGateway searchResultsHandler;
    private final Configuration configuration;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public RelatedItemSearchEventHandler(Configuration config,
                                         RelatedItemSearchRepository searcher,
                                         RelatedItemSearchResultsToResponseGateway handler) {
        this.searchRespository = searcher;
        this.configuration = config;
        this.searchResultsHandler = handler;
        this.searchMap = new HashMap<SearchRequestLookupKey, RelatedItemSearch>((int) Math.ceil(configuration.getSizeOfRelatedItemSearchRequestHandlerQueue() / 0.75));
    }

    @Override
    public void onEvent(RelatedItemSearch event, long sequence, boolean endOfBatch) throws Exception {
        SearchRequestLookupKey key = event.getLookupKey();
        log.debug("Handling search request for key {}", key.toString());

        if (searchMap.containsKey(key)) {
            // We already have the given search ready to process.
            // Just return.  We could do .put(key,event.copy), but there is no
            // point in creating the object copy if the event already exists. (that search has
            // already been requested)
            log.debug("Search for key {} already being processed", key.toString());
        } else {
            log.debug("Added search for key {}", key.toString());
            searchMap.put(key, event.copy(configuration));
        }


        if (endOfBatch) {
            try {
                RelatedItemSearch[] searches = new RelatedItemSearch[searchMap.size()];
                int i = 0;
                for (RelatedItemSearch r : searchMap.values()) {
                    searches[i++] = r;
                }
                log.debug("Executing search request for {} search(s)", searches.length);
                SearchResultEventWithSearchRequestKey[] results = searchRespository.findRelatedItems(configuration, searches);

                // Potentially Add the get product data call to here?

                searchResultsHandler.sendSearchResultsToResponseContexts(results);
            } finally {
                searchMap.clear();
            }
        }
    }

    @Override
    public void shutdown() {
        if(shutdown.compareAndSet(false,true)) {
           log.info("Attempting to shut down search repository");
           try {
               searchRespository.shutdown();
           } catch(Exception e) {
               log.error("Unable to shutdown search repository");
           }
        }

    }
}
