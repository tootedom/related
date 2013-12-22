package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorVararg;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.util.Map;

/**
 * Implementation that converts an
 * incoming user search request into a RelatedProductSearchRequest object.
 */
public class RelatedProductSearchRequestTranslator implements IncomingSearchRequestTranslator {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchRequestTranslator.class);

    private final RelatedProductSearchFactory relatedProductSearchFactory;

    public RelatedProductSearchRequestTranslator(RelatedProductSearchFactory relatedProductSearchFactory) {
        this.relatedProductSearchFactory = relatedProductSearchFactory;
    }

    @Override
    public void translateTo(RelatedProductSearchRequest event, long sequence,
                            RelatedProductSearchType type, Map<String,String> params,
                            AsyncContext context) {
        log.debug("Creating Related Product Search Request {}, {}",event.getSearchRequest().getLookupKey(),params);
        event.setRequestContext(context);
        relatedProductSearchFactory.populateSearchObject(event.getSearchRequest(), type,params);
    }
}
