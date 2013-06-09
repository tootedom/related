package org.greencheek.relatedproduct.util.config;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.RelatedProductStorageRepository;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public interface ApplicationCtx {
    public RelatedProductIndexRequestProcessor getIndexRequestProcessor();
    public RelatedProductStorageRepository getStorageRepository();
    public Configuration getConfiguration();
}
