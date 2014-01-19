package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.domain.api.SearchResultEventWithSearchRequestKey;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Event Handler that receives event from the ring buffer in the form of {@link RelatedProductSearch} objects.
 * This are the sent in batches to the {@link RelatedProductSearchRepository} that performs the users search,
 * returning the batch of search results.  The {@link org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway} is called
 * to deal with processing those results.
 */
public class RelatedProductSearchEventHandler implements RelatedProductSearchDisruptorEventHandler {


    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchEventHandler.class);

    private final Map<SearchRequestLookupKey, RelatedProductSearch> searchMap;
    private final RelatedProductSearchRepository searchRespository;
    private final RelatedProductSearchResultsToResponseGateway searchResultsHandler;
    private final Configuration configuration;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public RelatedProductSearchEventHandler(Configuration config,
                                            RelatedProductSearchRepository searcher,
                                            RelatedProductSearchResultsToResponseGateway handler) {
        this.searchRespository = searcher;
        this.configuration = config;
        this.searchResultsHandler = handler;
        this.searchMap = new HashMap<SearchRequestLookupKey, RelatedProductSearch>((int) Math.ceil(configuration.getSizeOfRelatedContentSearchRequestHandlerQueue() / 0.75));
    }

    @Override
    public void onEvent(RelatedProductSearch event, long sequence, boolean endOfBatch) throws Exception {
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
                RelatedProductSearch[] searches = new RelatedProductSearch[searchMap.size()];
                int i = 0;
                for (RelatedProductSearch r : searchMap.values()) {
                    searches[i++] = r;
                }
                log.debug("Executing search request for {} search(s)", searches.length);
                SearchResultEventWithSearchRequestKey[] results = searchRespository.findRelatedProducts(configuration, searches);
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
