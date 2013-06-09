package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperty;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchTranslator implements EventTranslator<RelatedProductSearch> {

    private final RelatedProductSearch searchRequest;

    public RelatedProductSearchTranslator(RelatedProductSearch searchRequest) {
        this.searchRequest = searchRequest;
    }

    @Override
    public void translateTo(RelatedProductSearch event, long sequence) {
        event.maxResults.set(searchRequest.maxResults.get());
        event.relatedContentId.set(searchRequest.relatedContentId.get());

        short numOfProps = searchRequest.additionalSearchCriteria.numberOfProperties.get();
        event.additionalSearchCriteria.numberOfProperties.set(numOfProps);

        for(int i=0;i<numOfProps;i++) {
            RelatedProductAdditionalProperty prop = searchRequest.additionalSearchCriteria.additionalProperties[i];

            event.additionalSearchCriteria.additionalProperties[i].name.set(prop.name.get());
            event.additionalSearchCriteria.additionalProperties[i].value.set(prop.value.get());
        }

        event.searchType.set(searchRequest.searchType.get());

    }
}
