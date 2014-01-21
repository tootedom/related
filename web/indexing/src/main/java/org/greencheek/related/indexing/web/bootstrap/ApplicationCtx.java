package org.greencheek.related.indexing.web.bootstrap;

import org.greencheek.related.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.related.util.config.Configuration;

/**
 *
 */
public interface ApplicationCtx {
    public IndexRequestProcessorFactory getIndexRequestProcessorFactory();
    public Configuration getConfiguration();
}
