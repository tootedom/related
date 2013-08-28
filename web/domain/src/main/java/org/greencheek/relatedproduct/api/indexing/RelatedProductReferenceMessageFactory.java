package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.domain.RelatedProductReference;


/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductReferenceMessageFactory implements EventFactory<RelatedProductReference> {


    public RelatedProductReferenceMessageFactory() {

    }

    @Override
    public RelatedProductReference newInstance() {
        RelatedProductReference message = new RelatedProductReference();
        return message;
    }
}
