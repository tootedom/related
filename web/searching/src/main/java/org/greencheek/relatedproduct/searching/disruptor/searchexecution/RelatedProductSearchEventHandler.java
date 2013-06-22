package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestResponseProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchEventHandler implements RelatedProductSearchDisruptorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchEventHandler.class);

    private final Map<SearchRequestLookupKey,RelatedProductSearch> searchMap;
    private final RelatedProductSearchRepository searchRespository;
    private final RelatedProductSearchRequestResponseProcessor searchResultsHandler;
    private final Configuration configuration;

    public RelatedProductSearchEventHandler(Configuration config,
                                            RelatedProductSearchRepository searcher,
                                            RelatedProductSearchRequestResponseProcessor handler) {
        this.searchRespository = searcher;
        this.configuration = config;
        this.searchResultsHandler = handler;
        this.searchMap = new HashMap<SearchRequestLookupKey, RelatedProductSearch>(configuration.getSizeOfRelatedContentSearchRequestHandlerQueue());
    }

    @Override
    public void onEvent(RelatedProductSearch event, long sequence, boolean endOfBatch) throws Exception {
        if(!event.validMessage.get()) return;

        try {
            SearchRequestLookupKey key = event.getLookupKey(configuration);
            log.debug("Handling search request for key {}",key.toString());
            if(!searchMap.containsKey(key)) {
                // need to copy
                searchMap.put(key,event.copy(configuration));
            } else {
                // We already have the given search ready to process.
                // Just return
                log.debug("Search for key {} , is already being executed",key.toString());
                return;
            }


            if(endOfBatch) {
                try {
                    RelatedProductSearch[] searches = new RelatedProductSearch[searchMap.size()];
                    searches = searchMap.values().toArray(searches);
                    searchRespository.findRelatedProducts(searches,searchResultsHandler);
                } finally {
                    searchMap.clear();
                }
            }

        } finally {
            event.validMessage.set(false);
        }
    }
}
