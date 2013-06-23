package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.util.ISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 06/06/2013
 * Time: 21:00
 * To change this template use File | Settings | File Templates.
 */
@Named
public class JsonSmartIndexingRequestConverterFactory implements IndexingRequestConverterFactory {

    private final ISO8601UTCCurrentDateAndTimeFormatter dateCreator;

    @Inject
    public JsonSmartIndexingRequestConverterFactory(ISO8601UTCCurrentDateAndTimeFormatter formatter) {
        this.dateCreator = formatter;
    }

    @Override
    public IndexingRequestConverter createConverter(Configuration configuration, byte[] convertFrom) throws InvalidIndexingRequestException
    {
        JsonSmartIndexingRequestConverter converter = new JsonSmartIndexingRequestConverter(configuration,dateCreator,convertFrom);
        return converter;
    }


}
