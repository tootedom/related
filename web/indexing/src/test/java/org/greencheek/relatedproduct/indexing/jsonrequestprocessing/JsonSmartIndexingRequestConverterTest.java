package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
public class JsonSmartIndexingRequestConverterTest extends IndexingRequestConverterTest {

    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request) {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request);
    }

    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request, int maxNumberOfAllowedProperties) {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request,maxNumberOfAllowedProperties);
    }
}
