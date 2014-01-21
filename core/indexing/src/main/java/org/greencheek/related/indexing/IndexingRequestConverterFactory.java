package org.greencheek.related.indexing;

import org.greencheek.related.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 06/06/2013
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
public interface IndexingRequestConverterFactory {
    public IndexingRequestConverter createConverter(Configuration config,
                                                    ByteBuffer convertFrom) throws InvalidIndexingRequestException;
}
