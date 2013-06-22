package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.util.config.Configuration;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductInitialIndexingRequestTranslator implements EventTranslator<RelatedProductIndexingMessage> {

    private final IndexingRequestConverter requestConverter;
    private final short maxNumberOfProductRelatedProperties;

    public RelatedProductInitialIndexingRequestTranslator(Configuration configuration, IndexingRequestConverter converter) {
        this.maxNumberOfProductRelatedProperties = configuration.getMaxNumberOfRelatedProductProperties();
        this.requestConverter = converter;
    }


    @Override
    public void translateTo(RelatedProductIndexingMessage event, long sequence) {

        event.validMessage.set(true);
        try {
            requestConverter.convertRequestIntoIndexingMessage(event,maxNumberOfProductRelatedProperties);
        } finally {
            event.validMessage.set(false);
        }



    }
}
