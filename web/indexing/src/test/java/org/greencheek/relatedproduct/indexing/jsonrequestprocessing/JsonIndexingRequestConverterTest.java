package org.greencheek.relatedproduct.indexing.jsonrequestprocessing;

import org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage;
import org.greencheek.relatedproduct.indexing.IndexingRequestConverter;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestException;
import org.greencheek.relatedproduct.indexing.InvalidIndexingRequestNoProductsFoundException;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the conversion of a json indexing request into a RelatedProductIndexingMessage
 */
public abstract class JsonIndexingRequestConverterTest {

    private RelatedProductIndexingMessage message;



    public abstract IndexingRequestConverter createConverter(ByteBuffer data);
    public abstract IndexingRequestConverter createConverter(ByteBuffer data, int numberOfAllowedProperties, int maxNumberOfRelatedProductsPerPurchase);


    @Before
    public void setUp() {
        System.setProperty("related-product.discard.storage.requests.with.too.many.relations", "false");
        System.setProperty("related-product.max.number.related.products.per.product","10");
        message = new RelatedProductIndexingMessage(new SystemPropertiesConfiguration());
    }

    @After
    public void tearDown() {
        System.clearProperty("related-product.discard.storage.requests.with.too.many.relations");
        System.clearProperty("related-product.max.number.related.products.per.product");
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testProductsIsNotAnArray() {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : { \"p\" : [\"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\"] }"+
                        "}";

        thrown.expect(InvalidIndexingRequestException.class);
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
    }

    @Test
    public void testDateIsGenerated() throws Exception {

        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"products\" : [ \"B009S4IJCK\",  \"B0076UICIO\" ,\"B0096TJCXW\" ]"+
                        "}";

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        assertNotNull(message.getUTCFormattedDate());
        assertTrue(message.getUTCFormattedDate().startsWith(f.format(new Date())));
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

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);
        assertEquals("Message should be valid, 3 related products",true,message.isValidMessage());
        assertEquals("Message should have 3 related products",3,message.getRelatedProducts().getNumberOfRelatedProducts());

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

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);
        assertEquals("Message should be valid, 3 related products",true,message.isValidMessage());
        assertEquals("Message should have 3 related products",3,message.getRelatedProducts().getNumberOfRelatedProducts());

    }

    @Test(expected=InvalidIndexingRequestException.class)
    public void testRequestWithNoProductsResultsInInvalidIndexingMessage() throws Exception {
        String json =
                "{" +
                        "    \"channel\" : \"uk\"," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\""+
                        "}";

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);
        assertEquals("Message should be invalid, no related products",false,message.isValidMessage());
        assertEquals("Message should have no related product",0,message.getRelatedProducts().getNumberOfRelatedProducts());


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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);
        assertEquals("Message should be invalid, no related products",false,message.isValidMessage());
        assertEquals("Message should have no related product",0,message.getRelatedProducts().getNumberOfRelatedProducts());


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
        try {
            IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
            fail("A No Products Found Exception should be generated");
//            converter.translateTo(message,(short)10);
//            assertEquals("Message should be invalid, no related products",false,message.isValidMessage());
//            assertEquals("Message should have no related product",0,message.getRelatedProducts().getNumberOfRelatedProducts());
        } catch(InvalidIndexingRequestNoProductsFoundException e) {

        }


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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);

        assertEquals("Message should be valid, with 2 related products",true,message.isValidMessage());
        assertEquals("Message should have 2 related products",2,message.getRelatedProducts().getNumberOfRelatedProducts());


        Map<String,String> properties = new HashMap<String,String>(message.getIndexingMessageProperties().getNumberOfProperties());

        message.getIndexingMessageProperties().convertTo(properties);

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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()),1,10);
        converter.translateTo(message,(short)1);

        assertEquals("Message should be valid, with 2 related products",true,message.isValidMessage());
        assertEquals("Message should have 2 related products",2,message.getRelatedProducts().getNumberOfRelatedProducts());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.getIndexingMessageProperties().convertTo(properties);

        assertEquals(1,properties.size());

        converter = createConverter(ByteBuffer.wrap(json.getBytes()),3,2);
        converter.translateTo(message,(short)2);

        message.getIndexingMessageProperties().convertTo(properties);

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

        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);

        assertEquals("Message should be invalid, no related products",true,message.isValidMessage());
        assertEquals("Message should have 3 related product",3,message.getRelatedProducts().getNumberOfRelatedProducts());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.getRelatedProducts().getRelatedProductAtIndex(0).getAdditionalProperties().convertTo(properties);

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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));

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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);

        assertEquals("Message should be valid, with 3 related products",true,message.isValidMessage());
        assertEquals("Message should have 3 related products",3,message.getRelatedProducts().getNumberOfRelatedProducts());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.getIndexingMessageProperties().convertTo(properties);

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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()),1,3);
        converter.translateTo(message,(short)1);

        assertEquals("Message should be valid, with 3 related products",true,message.isValidMessage());
        assertEquals("Message should have 2 related products",2,message.getRelatedProducts().getNumberOfRelatedProducts());


        Map<String,String> properties = new HashMap<String,String>(1);

        message.getIndexingMessageProperties().convertTo(properties);

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
        IndexingRequestConverter converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);

        assertEquals("Message should be valid, with 1 related products",true,message.isValidMessage());
        assertEquals("Message should have 1 related products",1,message.getRelatedProducts().getNumberOfRelatedProducts());


        json =
                "{" +
                        "    \"channel\" : 1," +
                        "    \"site\" : \"amazon\"," +
                        "    \"date\" : \"2013-05-02T15:31:31\","+
                        "    \"products\" : [  1 ,\"B0096TJCXW\"  ]"+
                        "}";
        converter = createConverter(ByteBuffer.wrap(json.getBytes()));
        converter.translateTo(message,(short)10);

        assertEquals("Message should be valid, with 1 related products",true,message.isValidMessage());
        assertEquals("Message should have 1 related products",1,message.getRelatedProducts().getNumberOfRelatedProducts());

    }
}
