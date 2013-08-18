package org.greencheek.relatedproduct.indexing.requestprocessors.multi.disruptor;

import com.lmax.disruptor.EventTranslator;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class CopyingRelatedProductIndexMessageTranslator implements EventTranslator<RelatedProductIndexingMessage>  {

    private static final Logger log = LoggerFactory.getLogger(CopyingRelatedProductIndexMessageTranslator.class);

    private final RelatedProductIndexingMessage sourceMessageToCopy;

    public CopyingRelatedProductIndexMessageTranslator(RelatedProductIndexingMessage message) {
        log.debug("is message to translate valid? {}",message.isValidMessage());
        this.sourceMessageToCopy = message;
    }

    @Override
    public void translateTo(RelatedProductIndexingMessage relatedProductIndexingMessage, long l) {
        log.debug("Translating message ready for indexing");
        sourceMessageToCopy.copyInto(relatedProductIndexingMessage);
        sourceMessageToCopy.setValidMessage(false);
    }
}
