package org.greencheek.related.indexing.requestprocessorfactory;

import org.greencheek.related.indexing.RelatedItemIndexRequestProcessor;
import org.greencheek.related.util.config.Configuration;

/**
 *
 */
public interface IndexRequestProcessorFactory {

    /**
     * Returns the processor that will take user requests, convert them to RelatedItem objects
     * and index/store them.
     *
     * @param configuration
     * @return
     */
    public RelatedItemIndexRequestProcessor createProcessor(Configuration configuration);

    /**
     * Shuts down the factory.
     */
    public void shutdown();
}
