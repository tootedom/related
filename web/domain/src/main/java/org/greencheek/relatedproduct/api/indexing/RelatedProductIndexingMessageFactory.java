package org.greencheek.relatedproduct.api.indexing;

import com.lmax.disruptor.EventFactory;
import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Factory for use by the Disruptor for filling the ring buffer with RelatedProductIndexingMessage
 */
public class RelatedProductIndexingMessageFactory implements EventFactory<RelatedProductIndexingMessage> {

    private final Configuration configuration;

    public RelatedProductIndexingMessageFactory(Configuration configuration) {
        this.configuration=configuration;
    }

    @Override
    public RelatedProductIndexingMessage newInstance() {
        return new RelatedProductIndexingMessage(configuration);
    }
}
