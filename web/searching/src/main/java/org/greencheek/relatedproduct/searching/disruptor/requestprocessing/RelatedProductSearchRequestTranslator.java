package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 10/06/2013
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchRequestTranslator implements EventTranslator<RelatedProductSearchRequest> {

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchRequestTranslator.class);


    private final AsyncContext clientCtx;
    private final Map<String,String> parameters;
    private final RelatedProductSearchType searchRequestType;
    private final Configuration configuration;

    public RelatedProductSearchRequestTranslator(Configuration configuration,
                                                 RelatedProductSearchType requestType,
                                                 Map<String, String> parameters,
                                                 AsyncContext context) {
        this.configuration = configuration;
        this.searchRequestType = requestType;
        this.parameters = new HashMap<String,String>(parameters);
        this.clientCtx = context;
    }
    @Override
    public void translateTo(RelatedProductSearchRequest event, long sequence) {
//        event.setRequestType(searchRequestType);
        event.setRequestContext(clientCtx);
        RelatedProductSearchFactory.populateSearchObject(configuration, event.searchRequest, searchRequestType,parameters);

        log.debug("Creating Related Product Search Request {}, {}",event.searchRequest.getLookupKey(configuration),parameters);

//        event.setRequestProperties(parameters);
    }
}
