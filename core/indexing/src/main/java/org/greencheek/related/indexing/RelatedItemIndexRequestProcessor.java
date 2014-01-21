package org.greencheek.related.indexing;

import org.greencheek.related.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Entry point for processing messages that relate products.
 */
public interface RelatedItemIndexRequestProcessor {

    public IndexRequestPublishingStatus processRequest(Configuration config, ByteBuffer indexRequestMessage);
    public void shutdown();
}
