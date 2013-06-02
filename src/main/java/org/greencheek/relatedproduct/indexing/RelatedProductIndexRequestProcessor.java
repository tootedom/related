package org.greencheek.relatedproduct.indexing;

/**
 * Entry point for processing messages that relate products.
 */
public interface RelatedProductIndexRequestProcessor {

    public void processRequest(byte[] indexRequestMessage);
    public void shutdown();
}
