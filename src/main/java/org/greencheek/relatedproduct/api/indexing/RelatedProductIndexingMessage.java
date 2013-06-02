package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;
import javolution.io.Struct;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductIndexingMessage extends Struct {

    public final Bool validMessage = new Bool();
    public final RelatedProductSet relatedProducts = inner(new RelatedProductSet());
    public final UTF8String purchaseDate = new UTF8String(28);
    public final RelatedProductAdditionalProperties additionalProperties = inner(new RelatedProductAdditionalProperties());


    public int getSize()
    {
       return this.size();
    }

    @Override
    public String toString()
    {
        return "RelatedProductIndexingMessage";
    }

    public final static EventFactory<RelatedProductIndexingMessage> FACTORY = new EventFactory<RelatedProductIndexingMessage>()
    {
        @Override
        public RelatedProductIndexingMessage newInstance()
        {
            RelatedProductIndexingMessage message = new RelatedProductIndexingMessage();
            message.setByteBuffer(ByteBuffer.allocate(message.size()), 0);
            return message;
        }
    };


}
