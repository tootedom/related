package org.greencheek.related.searching.disruptor.requestprocessing;

import org.greencheek.related.searching.RelatedItemSearchExecutorFactory;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 16/06/2013
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedContentSearchRequestProcessorHandlerFactory {
    RelatedContentSearchRequestProcessorHandler createHandler(Configuration config, RelatedItemSearchResultsToResponseGateway gateway,RelatedItemSearchExecutorFactory searchExecutorFactory);
}
