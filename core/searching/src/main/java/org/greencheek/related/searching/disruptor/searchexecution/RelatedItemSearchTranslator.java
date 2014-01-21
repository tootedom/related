package org.greencheek.related.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.related.api.searching.RelatedItemSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemSearchTranslator implements EventTranslatorOneArg<RelatedItemSearch,RelatedItemSearch> {

    public static final RelatedItemSearchTranslator INSTANCE = new RelatedItemSearchTranslator();

    public RelatedItemSearchTranslator() {
    }

    @Override
    public void translateTo(RelatedItemSearch event, long sequence,RelatedItemSearch searchRequest) {
        event.setMaxResults(searchRequest.getMaxResults());
        searchRequest.getRelatedContentIdentifier().copyTo(event.getRelatedContentIdentifier());
        searchRequest.getAdditionalSearchCriteria().copyTo(event.getAdditionalSearchCriteria());
        event.setRelatedItemSearchType(searchRequest.getRelatedItemSearchType());
        event.setLookupKey(searchRequest.getLookupKey());
    }
}
