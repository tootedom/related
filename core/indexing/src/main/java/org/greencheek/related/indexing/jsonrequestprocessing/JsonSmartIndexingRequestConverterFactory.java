package org.greencheek.related.indexing.jsonrequestprocessing;

import org.greencheek.related.indexing.IndexingRequestConverter;
import org.greencheek.related.indexing.IndexingRequestConverterFactory;
import org.greencheek.related.indexing.InvalidIndexingRequestException;
import org.greencheek.related.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.util.config.Configuration;

import java.nio.ByteBuffer;

/**
 * Creates a Json Smart based IndexingRequestConverter, that will transform json into
 * RelatedItemIndexingMessage objects.
 */
public class JsonSmartIndexingRequestConverterFactory implements IndexingRequestConverterFactory {

    private final ISO8601UTCCurrentDateAndTimeFormatter dateCreator;

    public JsonSmartIndexingRequestConverterFactory(ISO8601UTCCurrentDateAndTimeFormatter formatter) {
        this.dateCreator = formatter;
    }

    @Override
    public IndexingRequestConverter createConverter(Configuration configuration, ByteBuffer convertFrom) throws InvalidIndexingRequestException
    {
        return new JsonSmartIndexingRequestConverter(configuration,dateCreator,convertFrom);
    }


}
