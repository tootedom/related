package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;

import java.nio.ByteBuffer;

/**
 * Tests that the JsonSmart implementation is able to parse the json documents into an RelatedProductIndexingMessage domain
 * object
 */
public class JsonSmartIndexingRequestConverterTest extends JsonIndexingRequestConverterTest {

    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request) {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request);
    }

    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request, int maxNumberOfAllowedProperties, int maxNumberOfRelatedProductsPerPurchase) {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request,maxNumberOfAllowedProperties,maxNumberOfRelatedProductsPerPurchase);
    }
}
