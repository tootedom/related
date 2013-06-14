package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRepository;
import org.greencheek.relatedproduct.searching.SearchRequestResponseHandler;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@Named
public class RelatedProductSearchEventHandler implements RelatedProductSearchDisruptorEventHandler {

    private final Map<SearchRequestLookupKey,RelatedProductSearch> searchMap;
    private final RelatedProductSearchRepository searchRespository;
    private final SearchRequestResponseHandler searchResultsHandler;
    private final Configuration configuration;

    @Inject
    public RelatedProductSearchEventHandler(Configuration config,
                                            RelatedProductSearchRepository searcher,
                                            SearchRequestResponseHandler handler) {
        this.searchRespository = searcher;
        this.configuration = config;
        this.searchResultsHandler = handler;
        this.searchMap = new HashMap<SearchRequestLookupKey, RelatedProductSearch>(configuration.getSizeOfRelatedContentSearchRequestHandlerQueue());
    }

    @Override
    public void onEvent(RelatedProductSearch event, long sequence, boolean endOfBatch) throws Exception {

        try {
            SearchRequestLookupKey key = event.getLookupKey(configuration);
            if(!searchMap.containsKey(key)) {
                // need to copy
                searchMap.put(key,event.copy(configuration));
            } else {
                // We already have the given search ready to process.
                // Just return

                return;
            }

            if(endOfBatch) {
                RelatedProductSearch[] searches = new RelatedProductSearch[searchMap.size()];
                searches = searchMap.values().toArray(searches);
                searchRespository.findRelatedProducts(searches,searchResultsHandler);
            }
        } finally {
            event.validMessage.set(false);
        }
    }
}
