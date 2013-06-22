package org.greencheek.relatedproduct.indexing;

import org.greencheek.relatedproduct.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 06/06/2013
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
public interface IndexingRequestConverterFactory {
    public IndexingRequestConverter createConverter(Configuration config,
                                                    byte[] convertFrom) throws InvalidIndexingRequestException;
}
