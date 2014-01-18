package org.greencheek.relatedproduct.indexing.requestprocessorfactory;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 *
 */
public interface IndexRequestProcessorFactory {

    /**
     * Returns the processor that will take user requests, convert them to RelatedProduct objects
     * and index/store them.
     *
     * @param configuration
     * @return
     */
    public RelatedProductIndexRequestProcessor createProcessor(Configuration configuration);

    /**
     * Shuts down the factory.
     */
    public void shutdown();
}
