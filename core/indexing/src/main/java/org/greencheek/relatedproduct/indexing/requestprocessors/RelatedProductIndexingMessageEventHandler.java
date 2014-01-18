package org.greencheek.relatedproduct.indexing.requestprocessors;

import com.lmax.disruptor.EventHandler;
import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;

/**
 * Event handler for dealing with RelatedProductIndexingMessage events.
 * This means the request is either sent directly to the repository that deals with
 * the indexing request (i.e. saves it), or passes it on to a further ring buffer for batching
 * and storage further on down the line.
 */
public interface RelatedProductIndexingMessageEventHandler extends EventHandler<RelatedProductIndexingMessage> {

    public void shutdown();
}
