package org.greencheek.relatedproduct.api.indexing;

import javolution.io.Struct;
import org.greencheek.relatedproduct.api.RelatedProductAdditionalProperties;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductInfo extends Struct {
    public final Struct.UTF8String id;
    public final RelatedProductAdditionalProperties additionalProperties;

    public RelatedProductInfo(Configuration configuration) {
        id = new UTF8String(configuration.getRelatedProductIdLength());
        additionalProperties = inner(new RelatedProductAdditionalProperties(configuration,configuration.getMaxNumberOfRelatedProductProperties()));

    }

}
