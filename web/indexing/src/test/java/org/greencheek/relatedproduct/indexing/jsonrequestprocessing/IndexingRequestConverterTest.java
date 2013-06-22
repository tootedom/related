package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public abstract class IndexingRequestConverterTest {


    private IndexingRequestConverter converter;
    private RelatedProductIndexingMessage message;



    public abstract IndexingRequestConverter createConverter(byte[] data);

    @Before
    public void setUp() {

        message = new RelatedProductIndexingMessage(new SystemPropertiesConfiguration());
    }


    @Test
    public void testConvertRequestWithIdArrayObjectsIntoIndexingMessage() throws Exception {

        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);
        assertEquals("Message should be valid, 3 related products",true,message.validMessage.get());
        assertEquals("Message should have 3 related products",3,message.relatedProducts.numberOfRelatedProducts.get());

    }

    @Test
    public void testConvertRequestWithProductArrayObjectsIntoIndexingMessage() throws Exception {
        String json =
        "{" +
        "    \"channel\" : \"uk\"," +
        "    \"site\" : \"amazon\"," +
        "    \"date\" : \"2013-05-02T15:31:31\","+
        "    \"products\" : [ { \"id\" : \"B009S4IJCK\"}, { \"id\" : \"B0076UICIO\"}, { \"id\" : \"B0096TJCXW\"} ]"+
        "}";

        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);
        assertEquals("Message should be valid, 3 related products",true,message.validMessage.get());
        assertEquals("Message should have 3 related products",3,message.relatedProducts.numberOfRelatedProducts.get());

    }

    @Test(expected=InvalidIndexingRequestException.class)
    public void testRequestWithNoProductsResultsInInvalidIndexingMessage() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\""+
                        "}";

        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);
        assertEquals("Message should be invalid, no related products",false,message.validMessage.get());
        assertEquals("Message should have no related product",0,message.relatedProducts.numberOfRelatedProducts.get());


    }

    @Test
    public void testRequestWithNoProductsInObjectArrayResultsInInvalidIndexingMessage() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ { }, { }, { } ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);
        assertEquals("Message should be invalid, no related products",false,message.validMessage.get());
        assertEquals("Message should have no related product",0,message.relatedProducts.numberOfRelatedProducts.get());


    }

    @Test
    public void testRequestWithEmptyProductsArrayResultsInInvalidIndexingMessage() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);

        assertEquals("Message should be invalid, no related products",false,message.validMessage.get());
        assertEquals("Message should have no related product",0,message.relatedProducts.numberOfRelatedProducts.get());

    }

    @Test
    public void testRequestAdditionalPropertiesInIndexRequestAreStored() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ \"10\",\"20\" ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);

        assertEquals("Message should be valid, with 2 related products",true,message.validMessage.get());
        assertEquals("Message should have 2 related products",2,message.relatedProducts.numberOfRelatedProducts.get());


        Map<String,String> properties = new HashMap<String,String>(message.additionalProperties.numberOfProperties.get());

        message.additionalProperties.convertTo(properties);

        assertTrue(properties.containsKey("channel"));
        assertTrue(properties.containsKey("site"));


    }

    @Test
    public void testRequestAdditionalPropertiesAreLimitedInIndexRequestAreStored() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ \"10\",\"20\" ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)1);

        assertEquals("Message should be valid, with 2 related products",true,message.validMessage.get());
        assertEquals("Message should have 2 related products",2,message.relatedProducts.numberOfRelatedProducts.get());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.additionalProperties.convertTo(properties);

        assertEquals(1,properties.size());

        converter.convertRequestIntoIndexingMessage(message,(short)2);

        message.additionalProperties.convertTo(properties);

        assertEquals(2,properties.size());
    }

    @Test
    public void testRequestWithProductObjectsWithPropertiesResultsInIndexingMessageWithThoseProperties() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ { \"id\" : \"B009S4IJCK\", \"type\":\"memory\" }, { \"id\" : \"B0076UICIO\", \"type\":\"staticdischarger\"  }, { \"id\" : \"B0096TJCXW\" ,\"type\":\"screwdriver\" } ]"+
                        "}";

        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);

        assertEquals("Message should be invalid, no related products",true,message.validMessage.get());
        assertEquals("Message should have 3 related product",3,message.relatedProducts.numberOfRelatedProducts.get());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.relatedProducts.relatedProducts[0].additionalProperties.convertTo(properties);

        assertEquals("Should have 1 additional properties: type",1,properties.size());
    }

    @Test(expected=InvalidIndexingRequestException.class)
    public void testRequestWithInvalidJsonData() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ { \"id\" : \"B009S4IJCK\", \"type\":\"memory\" }, { \"id\" : \"B0076UICIO\", \"type\":\"staticdischarger\"  }, { \"id\" : \"B0096TJCXW\" ,\"type\":\"screwdriver\" } ]"+
                        "...";
        IndexingRequestConverter converter = createConverter(json.getBytes());

    }

    @Test
    public void testRequestPropertiesWithNonStringBasedValuesAreIgnored() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : 1," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ { \"id\" : \"B009S4IJCK\", \"type\":\"memory\" }, { \"id\" : \"B0076UICIO\", \"type\":\"staticdischarger\"  }, { \"id\" : \"B0096TJCXW\" ,\"type\":\"screwdriver\" } ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);

        assertEquals("Message should be valid, with 3 related products",true,message.validMessage.get());
        assertEquals("Message should have 3 related products",3,message.relatedProducts.numberOfRelatedProducts.get());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.additionalProperties.convertTo(properties);

        assertEquals(1,properties.size());

    }


    @Test
    public void testRequestWithAnEmptyProductElementThenThatProductElementIsIgnored() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : 1," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ { \"id\" : \"B009S4IJCK\", \"type\":\"memory\" }, { }, { \"id\" : \"B0096TJCXW\" ,\"type\":\"screwdriver\" } ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)1);

        assertEquals("Message should be valid, with 3 related products",true,message.validMessage.get());
        assertEquals("Message should have 2 related products",2,message.relatedProducts.numberOfRelatedProducts.get());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.additionalProperties.convertTo(properties);

        assertEquals(1,properties.size());

    }

    @Test
    public void testRequestWithANonStringProductIdThatThatProductIsIgnored() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : 1," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [ { \"id\" : 1, \"type\":\"memory\" }, { }, { \"id\" : \"B0096TJCXW\" ,\"type\":\"screwdriver\" } ]"+
                        "}";
        IndexingRequestConverter converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);

        assertEquals("Message should be valid, with 1 related products",true,message.validMessage.get());
        assertEquals("Message should have 1 related products",1,message.relatedProducts.numberOfRelatedProducts.get());


        json =
                "{" +
                        "    \"channel\" : 1," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [  1 ,\"B0096TJCXW\"  ]"+
                        "}";
        converter = createConverter(json.getBytes());
        converter.convertRequestIntoIndexingMessage(message,(short)10);

        assertEquals("Message should be valid, with 1 related products",true,message.validMessage.get());
        assertEquals("Message should have 1 related products",1,message.relatedProducts.numberOfRelatedProducts.get());

    }
}
