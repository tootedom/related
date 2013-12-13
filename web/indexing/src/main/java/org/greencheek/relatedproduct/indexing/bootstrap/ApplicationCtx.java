package org.greencheek.relatedproduct.indexing.bootstrap;

import org.greencheek.relatedproduct.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 *
 */
public interface ApplicationCtx {
    public IndexRequestProcessorFactory getIndexRequestProcessorFactory();
    public Configuration getConfiguration();
}
