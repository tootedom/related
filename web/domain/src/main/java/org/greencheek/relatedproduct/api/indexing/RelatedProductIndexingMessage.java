package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;
import javolution.io.Struct;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductIndexingMessage extends Struct {

    public final Bool validMessage;
    public final RelatedProductSet relatedProducts;
    public final UTF8String purchaseDate;
    public final RelatedProductAdditionalProperties additionalProperties;


    public RelatedProductIndexingMessage(Configuration config) {
        validMessage = new Bool();
        relatedProducts = inner(new RelatedProductSet(config));
        purchaseDate = new UTF8String(28);
        additionalProperties = inner(new RelatedProductAdditionalProperties(config,config.getMaxNumberOfRelatedProductProperties()));

    }

    public int getSize()
    {
       return this.size();
    }

    @Override
    public String toString()
    {
        return "RelatedProductIndexingMessage";
    }




}
