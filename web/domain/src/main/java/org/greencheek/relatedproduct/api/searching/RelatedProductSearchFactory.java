package org.greencheek.relatedproduct.api.searching;

import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchFactory {

    private final Configuration configuration;

    public RelatedProductSearchFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public RelatedProductSearch createSearchObject() {
        RelatedProductSearch ob = new RelatedProductSearch(this.configuration);
        ob.setByteBuffer(ByteBuffer.allocate(ob.size()),0);
        return ob;
    }
}
