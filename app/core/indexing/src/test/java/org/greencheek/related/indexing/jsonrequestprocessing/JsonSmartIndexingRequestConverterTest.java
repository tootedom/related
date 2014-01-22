package org.greencheek.related.indexing.jsonrequestprocessing;

import org.greencheek.related.api.indexing.RelatedItemIndexingMessage;
import org.greencheek.related.indexing.IndexingRequestConverter;
import org.greencheek.related.indexing.InvalidIndexingRequestException;
import org.greencheek.related.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests that the JsonSmart implementation is able to parse the json documents into an RelatedItemIndexingMessage domain
 * object
 */
public class JsonSmartIndexingRequestConverterTest extends JsonIndexingRequestConverterTest {



    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request) throws InvalidIndexingRequestException {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request);
    }

    @Override
    public IndexingRequestConverter createConverter(ByteBuffer request, int maxNumberOfAllowedProperties, int maxNumberOfRelatedItemsPerRelation) throws InvalidIndexingRequestException {
        return new JsonSmartIndexingRequestConverter(new SystemPropertiesConfiguration(),new JodaISO8601UTCCurrentDateAndTimeFormatter(),request,maxNumberOfAllowedProperties, maxNumberOfRelatedItemsPerRelation);
    }

    @After
    public void tearDown() {
        super.tearDown();
        System.clearProperty(ConfigurationConstants.PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS);
        System.clearProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM);
    }


    @Test
    public void testANonStringPropertyIsIgnored() {

        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"items\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(new SystemPropertiesConfiguration());
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
        System.setProperty(ConfigurationConstants.PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS,"true");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM,"1");
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\"," +
                        "    \"items\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        try {
            IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
            fail("Should not be able to parse json, as it has too many related items");
        } catch(InvalidIndexingRequestException e) {

        }
    }

    @Test
    public void testExceptionIsNotThrownWhenJsonContainsTooManyProducts() {
        System.setProperty(ConfigurationConstants.PROPNAME_DISCARD_INDEXING_REQUESTS_WITH_TOO_MANY_ITEMS,"false");
        System.setProperty(ConfigurationConstants.PROPNAME_MAX_NO_OF_RELATED_ITEMS_PER_ITEM,"2");
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\"," +
                        "    \"items\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        try {
            IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
            RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(new SystemPropertiesConfiguration());
            converter.translateTo(message,1);

            assertEquals(2, message.getRelatedItems().getNumberOfRelatedItems());

            if(message.getRelatedItems().getRelatedItemAtIndex(0).getId().toString().equals("B009S4IJCK")) {
                assertEquals("B0076UICIO",message.getRelatedItems().getRelatedItemAtIndex(1).getId().toString());
            } else if(message.getRelatedItems().getRelatedItemAtIndex(0).getId().toString().equals("B0076UICIO")) {
                assertEquals("B009S4IJCK",message.getRelatedItems().getRelatedItemAtIndex(1).getId().toString());
            } else {
                fail("Json message should have thrown away the last id.");
            }
        } catch(InvalidIndexingRequestException e) {
            fail("Should be able to parse json, just that some related items are not stored");
        }
    }
}
