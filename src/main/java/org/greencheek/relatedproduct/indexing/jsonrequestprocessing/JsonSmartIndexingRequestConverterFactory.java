package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverterFactory;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;

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

    @Override
    public IndexingRequestConverter createConverter(byte[] convertFrom) throws InvalidIndexingRequestException
    {
        JsonSmartIndexingRequestConverter converter = new JsonSmartIndexingRequestConverter(convertFrom);
        return converter;
    }


}
