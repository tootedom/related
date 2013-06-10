package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.domain.api.SearchEvent;

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

    private final AsyncContext clientCtx;
    private final Map<String,String> parameters;
    private final RelatedProductSearchType searchRequestType;

    public RelatedProductSearchRequestTranslator(RelatedProductSearchType requestType,
                                                 Map<String, String> parameters,
                                                 AsyncContext context) {
        this.searchRequestType = requestType;
        this.parameters = new HashMap<String,String>(parameters);
        this.clientCtx = context;
    }
    @Override
    public void translateTo(RelatedProductSearchRequest event, long sequence) {

        event.setRequestType(searchRequestType);
        event.setRequestContext(clientCtx);
        event.setRequestProperties(parameters);
    }
}
