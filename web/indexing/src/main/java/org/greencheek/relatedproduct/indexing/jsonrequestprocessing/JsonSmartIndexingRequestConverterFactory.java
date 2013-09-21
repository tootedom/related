package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 06/06/2013
 * Time: 21:00
 * To change this template use File | Settings | File Templates.
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
