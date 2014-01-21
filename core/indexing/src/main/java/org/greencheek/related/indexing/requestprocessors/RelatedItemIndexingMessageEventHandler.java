package org.greencheek.related.indexing.requestprocessors;

import com.lmax.disruptor.EventHandler;
import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;

/**
 * Event handler for dealing with RelatedItemIndexingMessage events.
 * This means the request is either sent directly to the repository that deals with
 * the indexing request (i.e. saves it), or passes it on to a further ring buffer for batching
 * and storage further on down the line.
 */
public interface RelatedItemIndexingMessageEventHandler extends EventHandler<RelatedItemIndexingMessage> {

    public void shutdown();
}
