package org.greencheek.relatedproduct.searching.disruptor.requestresponse;

import com.lmax.disruptor.EventTranslatorTwoArg;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.api.SearchResponseEvent;
import org.greencheek.relatedproduct.searching.domain.api.SearchEventType;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SearchRequestTranslator implements EventTranslatorTwoArg<SearchResponseEvent,RelatedProductSearchRequest,RelatedProductSearchExecutor> {

    public static final SearchRequestTranslator INSTANCE = new SearchRequestTranslator();

    public SearchRequestTranslator() {

    }

    @Override
    public void translateTo(SearchResponseEvent event, long sequence, RelatedProductSearchRequest searchRequest,
                            RelatedProductSearchExecutor searchExecutor) {
//        event.getSearchRequestEvent().populateSearchRequestEvent(searchRequest.getRequestContext(),searchRequest.getSearchRequest(),searchExecutor);
//        event.setEventType(SearchEventType.SEARCH_REQUEST);
        event.setRequestKeyReference(searchRequest.getSearchRequest().getLookupKey());
    }
}
