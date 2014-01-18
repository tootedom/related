package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Entry point for processing messages that relate products.
 */
public interface RelatedProductIndexRequestProcessor {

    public IndexRequestPublishingStatus processRequest(Configuration config, ByteBuffer indexRequestMessage);
    public void shutdown();
}
