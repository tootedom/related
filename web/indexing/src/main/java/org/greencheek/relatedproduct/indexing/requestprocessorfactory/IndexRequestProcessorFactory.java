package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 15/06/2013
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public interface IndexRequestProcessorFactory {
    public RelatedProductIndexRequestProcessor createProcessor(Configuration configuration);
}
