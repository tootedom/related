package org.greencheek.relatedproduct.searching.executor;

import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutorFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchResultsToResponseGateway;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.DisruptorBasedRelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.disruptor.searchexecution.RelatedProductSearchEventHandler;
import org.greencheek.relatedproduct.searching.web.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created by dominictootell on 18/01/2014.
 */
public class SearchExecutorFactory implements RelatedProductSearchExecutorFactory {

    private final ApplicationCtx applicationCtx;

    public SearchExecutorFactory (ApplicationCtx ctx) {
        applicationCtx = ctx;
    }

    @Override
    public RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchResultsToResponseGateway gateway) {
        Configuration config = applicationCtx.getConfiguration();
        return new DisruptorBasedRelatedProductSearchExecutor(config,applicationCtx.createRelatedProductSearchEventFactory(),
                new RelatedProductSearchEventHandler(config,applicationCtx.createSearchRepository(),gateway));
    }
}
