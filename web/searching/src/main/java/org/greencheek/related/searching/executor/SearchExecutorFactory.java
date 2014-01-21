package org.greencheek.related.searching.executor;

import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.RelatedItemSearchExecutorFactory;
import org.greencheek.related.searching.RelatedItemSearchResultsToResponseGateway;
import org.greencheek.related.searching.disruptor.searchexecution.DisruptorBasedRelatedItemSearchExecutor;
import org.greencheek.related.searching.disruptor.searchexecution.RelatedItemSearchEventHandler;
import org.greencheek.related.searching.web.bootstrap.ApplicationCtx;
import org.greencheek.related.util.config.Configuration;

/**
 * Created by dominictootell on 18/01/2014.
 */
public class SearchExecutorFactory implements RelatedItemSearchExecutorFactory {

    private final ApplicationCtx applicationCtx;

    public SearchExecutorFactory (ApplicationCtx ctx) {
        applicationCtx = ctx;
    }

    @Override
    public RelatedItemSearchExecutor createSearchExecutor(RelatedItemSearchResultsToResponseGateway gateway) {
        Configuration config = applicationCtx.getConfiguration();
        return new DisruptorBasedRelatedItemSearchExecutor(config,applicationCtx.createRelatedItemSearchEventFactory(),
                new RelatedItemSearchEventHandler(config,applicationCtx.createSearchRepository(),gateway));
    }
}
