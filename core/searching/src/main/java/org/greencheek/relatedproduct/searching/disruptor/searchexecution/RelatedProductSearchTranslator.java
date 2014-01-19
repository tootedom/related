package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchTranslator implements EventTranslatorOneArg<RelatedProductSearch,RelatedProductSearch> {

    public static final RelatedProductSearchTranslator INSTANCE = new RelatedProductSearchTranslator();

    public RelatedProductSearchTranslator() {
    }

    @Override
    public void translateTo(RelatedProductSearch event, long sequence,RelatedProductSearch searchRequest) {
        event.setMaxResults(searchRequest.getMaxResults());
        searchRequest.getRelatedContentIdentifier().copyTo(event.getRelatedContentIdentifier());
        searchRequest.getAdditionalSearchCriteria().copyTo(event.getAdditionalSearchCriteria());
        event.setRelatedProductSearchType(searchRequest.getRelatedProductSearchType());
        event.setLookupKey(searchRequest.getLookupKey());
    }
}
