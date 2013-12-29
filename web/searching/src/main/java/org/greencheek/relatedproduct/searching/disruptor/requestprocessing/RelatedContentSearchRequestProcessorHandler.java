package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;

/**
 *
 */
public interface RelatedContentSearchRequestProcessorHandler extends EventHandler<RelatedProductSearchRequest> {
    public void shutdown();
}
