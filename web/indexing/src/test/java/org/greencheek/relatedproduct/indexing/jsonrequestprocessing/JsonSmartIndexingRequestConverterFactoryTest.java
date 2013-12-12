package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.util.JodaISO8601UTCCurrentDateAndTimeFormatter;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/12/2013
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public class JsonSmartIndexingRequestConverterFactoryTest {

    @Test
    public void testJsonSmartConverterIsCreated()
    {
        JsonSmartIndexingRequestConverterFactory factory = new JsonSmartIndexingRequestConverterFactory(new JodaISO8601UTCCurrentDateAndTimeFormatter());

        try {
            factory.createConverter(new SystemPropertiesConfiguration(), ByteBuffer.wrap(new byte[0]));
            fail("Should not be able to create a converter that deals with no data");
        } catch(InvalidIndexingRequestException e) {

        }

        String json =
                "{" +
                        "    \"channel\" : 1.0," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";


        IndexingRequestConverter converter = factory.createConverter(new SystemPropertiesConfiguration(), ByteBuffer.wrap(json.getBytes()));

        assertTrue(converter instanceof JsonSmartIndexingRequestConverter);
    }
}
