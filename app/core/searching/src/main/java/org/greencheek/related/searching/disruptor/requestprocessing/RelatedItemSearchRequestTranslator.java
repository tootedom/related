package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.api.searching.RelatedItemSearchFactory;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Implementation that converts an
 * incoming user search request into a RelatedItemSearchRequest object.
 */
public class RelatedItemSearchRequestTranslator implements IncomingSearchRequestTranslator {

    private static final Logger log = LoggerFactory.getLogger(RelatedItemSearchRequestTranslator.class);

    private final RelatedItemSearchFactory relatedItemSearchFactory;

    public RelatedItemSearchRequestTranslator(RelatedItemSearchFactory relatedItemSearchFactory) {
        this.relatedItemSearchFactory = relatedItemSearchFactory;
    }

    @Override
    public void translateTo(RelatedItemSearchRequest event, long sequence,
                            RelatedItemSearchType type, Map<String,String> params,
                            SearchResponseContext[] contexts) {
        log.debug("Creating Related Product Search Request {}, {}",event.getSearchRequest().getLookupKey(),params);
        event.setRequestContexts(contexts);

        relatedItemSearchFactory.populateSearchObject(event.getSearchRequest(), type,params);
    }
}
