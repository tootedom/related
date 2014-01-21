package org.greencheek.related.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventHandler;
import org.greencheek.related.searching.RelatedItemSearchExecutor;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;

/**
 *
 */
public interface RelatedContentSearchRequestProcessorHandler extends EventHandler<RelatedItemSearchRequest> {
    public void handleRequest(RelatedItemSearchRequest searchRequest, RelatedItemSearchExecutor searchExecutor);
    public void shutdown();
}
