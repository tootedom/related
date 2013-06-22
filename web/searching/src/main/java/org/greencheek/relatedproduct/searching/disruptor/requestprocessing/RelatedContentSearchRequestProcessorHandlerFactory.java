package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import org.greencheek.relatedproduct.searching.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedContentSearchRequestProcessorHandlerFactory {
    RelatedContentSearchRequestProcessorHandler createHandler(Configuration config, ApplicationCtx applicationCtx);
}
