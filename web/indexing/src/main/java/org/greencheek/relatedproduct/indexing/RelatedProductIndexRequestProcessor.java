package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Entry point for processing messages that relate products.
 */
public interface RelatedProductIndexRequestProcessor {

    public void processRequest(Configuration config, byte... indexRequestMessage);
    public void shutdown();
}
