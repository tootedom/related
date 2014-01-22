package org.greencheek.related.api.indexing;

import com.lmax.disruptor.EventFactory;
import org.greencheek.related.util.config.Configuration;

/**
 * Factory for use by the Disruptor for filling the ring buffer with RelatedItemIndexingMessage
 */
public class RelatedItemIndexingMessageFactory implements EventFactory<RelatedItemIndexingMessage> {

    private final Configuration configuration;

    public RelatedItemIndexingMessageFactory(Configuration configuration) {
        this.configuration=configuration;
    }

    @Override
    public RelatedItemIndexingMessage newInstance() {
        return new RelatedItemIndexingMessage(configuration);
    }
}
