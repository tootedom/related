package org.greencheek.relatedproduct.web;


import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public interface ApplicationCtx {
    public RelatedProductSearchRequestProcessor getRequestProcessor();
    public Configuration getConfiguration();
}
