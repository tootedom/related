package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.relatedproduct.api.indexing.RelatedProduct;
import org.greencheek.relatedproduct.api.indexing.RelatedProductReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class BatchCopyingRelatedProductIndexMessageTranslator implements EventTranslatorOneArg<RelatedProductReference,RelatedProduct> {

    private static final Logger log = LoggerFactory.getLogger(BatchCopyingRelatedProductIndexMessageTranslator.class);
    public static final BatchCopyingRelatedProductIndexMessageTranslator INSTANCE = new BatchCopyingRelatedProductIndexMessageTranslator();


    public BatchCopyingRelatedProductIndexMessageTranslator() {

    }

    @Override
    public void translateTo(RelatedProductReference event, long sequence, RelatedProduct sourceMessageToCopy) {
        event.setReference(sourceMessageToCopy);
    }
}
