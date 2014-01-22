package org.greencheek.related.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.greencheek.related.api.indexing.RelatedItem;
import org.greencheek.related.api.indexing.RelatedItemReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class BatchCopyingRelatedItemIndexMessageTranslator implements EventTranslatorOneArg<RelatedItemReference,RelatedItem> {

    private static final Logger log = LoggerFactory.getLogger(BatchCopyingRelatedItemIndexMessageTranslator.class);
    public static final BatchCopyingRelatedItemIndexMessageTranslator INSTANCE = new BatchCopyingRelatedItemIndexMessageTranslator();


    public BatchCopyingRelatedItemIndexMessageTranslator() {

    }

    @Override
    public void translateTo(RelatedItemReference event, long sequence, RelatedItem sourceMessageToCopy) {
        event.setReference(sourceMessageToCopy);
    }
}
