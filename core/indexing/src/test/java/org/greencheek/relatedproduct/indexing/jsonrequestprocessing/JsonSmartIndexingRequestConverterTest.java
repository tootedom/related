package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.ConfigurationConstants;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests that the JsonSmart implementation is able to parse the json documents into an RelatedProductIndexingMessage domain
 * object
 */
public class JsonSmartIndexingRequestConverterTest extends JsonIndexingRequestConverterTest {



    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request) throws InvalidIndexingRequestException {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request);
    }

    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request, int maxNumberOfAllowedProperties, int maxNumberOfRelatedProductsPerPurchase) throws InvalidIndexingRequestException {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request,maxNumberOfAllowedProperties,maxNumberOfRelatedProductsPerPurchase);
    }

    @After
    public void tearDown() {
        super.tearDown();
        System.clearProperty(ConfigurationConstants.PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS);
        System.clearProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST);
    }


    @Test
    public void testANonStringPropertyIsIgnored() {

        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(new SystemPropertiesConfiguration());
        converter.translateTo(message,1);
        assertEquals(1,message.getIndexingMessageProperties().getNumberOfProperties());
    }

    @Test
    public void testExceptionIsThrownWhenJsonContainsNoProducts() {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\"" +
                        "}";

        try {
            IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
            fail("Should not be able to parse json");
        } catch(InvalidIndexingRequestException e) {

        }
    }

    @Test
    public void testExceptionIsThrownWhenJsonContainsTooManyProducts() {
        System.setProperty(ConfigurationConstants.PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS,"true");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,"1");
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\"," +
                        "    \"products\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        try {
            IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
            fail("Should not be able to parse json, as it has too many related products");
        } catch(InvalidIndexingRequestException e) {

        }
    }

    @Test
    public void testExceptionIsNotThrownWhenJsonContainsTooManyProducts() {
        System.setProperty(ConfigurationConstants.PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_PRODUCTS,"false");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_PRODUCTS_PER_INDEX_REQUEST,"2");
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\"," +
                        "    \"products\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        try {
            IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
            RelatedProductIndexingMessage message = new RelatedProductIndexingMessage(new SystemPropertiesConfiguration());
            converter.translateTo(message,1);

            assertEquals(2, message.getRelatedProducts().getNumberOfRelatedProducts());

            if(message.getRelatedProducts().getRelatedProductAtIndex(0).getId().toString().equals("B009S4IJCK")) {
                assertEquals("B0076UICIO",message.getRelatedProducts().getRelatedProductAtIndex(1).getId().toString());
            } else if(message.getRelatedProducts().getRelatedProductAtIndex(0).getId().toString().equals("B0076UICIO")) {
                assertEquals("B009S4IJCK",message.getRelatedProducts().getRelatedProductAtIndex(1).getId().toString());
            } else {
                fail("Json message should have thrown away the last id.");
            }
        } catch(InvalidIndexingRequestException e) {
            fail("Should be able to parse json, just that some related products are not stored");
        }
    }
}
