package org.greencheek.related.api.indexing;

import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 11/12/2013
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class BasicRelatedItemIndexingMessageConverterTest {

    private final Configuration configuration = new SystemPropertiesConfiguration();
    private static final String DATE = "2013-05-22T20:31:35";
    private static final String PRODUCTID_1 = "1";
    private static final String PRODUCTID_2 = "2";
    private static final String PRODUCTID_3 = "3";

    private static final String SITE = "amazon";
    private static final String CHANNEL = "uk";
    private static final String DEPARTMENT = "electronics";
    private static final String SUBCATEGORY = "accessories";

    public RelatedItemIndexingMessageConverter getConverter() {
        return new BasicRelatedItemIndexingMessageConverter(configuration);
    }


    public RelatedItemIndexingMessage getIndexingMessageWithOneRelatedItem() {
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(configuration);

        message.setValidMessage(true);
        message.setUTCFormattedDate(DATE);
        message.getIndexingMessageProperties().addProperty("site", SITE);
        message.getIndexingMessageProperties().addProperty("channel",CHANNEL);
        message.getRelatedItems().setNumberOfRelatedItems(1);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).setId(PRODUCTID_1);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("department", DEPARTMENT);

        return message;
    }

    public RelatedItemIndexingMessage getIndexingMessageWithTwoRelatedItem() {
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(configuration);

        message.setValidMessage(true);
        message.setUTCFormattedDate(DATE);
        message.getIndexingMessageProperties().addProperty("site", SITE);
        message.getIndexingMessageProperties().addProperty("channel",CHANNEL);
        message.getRelatedItems().setNumberOfRelatedItems(2);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).setId(PRODUCTID_1);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("subcategory","laptops");
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("name","apple mac");

        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).setId(PRODUCTID_2);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("name","apple care insurance");

        return message;
    }

    public RelatedItemIndexingMessage getIndexingMessageWithThreeRelatedItem() {
        RelatedItemIndexingMessage message = new RelatedItemIndexingMessage(configuration);

        message.setValidMessage(true);
        message.setUTCFormattedDate(DATE);
        message.getIndexingMessageProperties().addProperty("site", SITE);
        message.getIndexingMessageProperties().addProperty("channel",CHANNEL);
        message.getRelatedItems().setNumberOfRelatedItems(3);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).setId(PRODUCTID_1);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("subcategory","laptops");
        message.getRelatedItems().getCheckedRelatedItemAtIndex(0).additionalProperties.addProperty("name","apple mac");

        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).setId(PRODUCTID_2);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(1).additionalProperties.addProperty("name","apple care insurance");

        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).setId(PRODUCTID_3);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).additionalProperties.addProperty("department", DEPARTMENT);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).additionalProperties.addProperty("subcategory",SUBCATEGORY);
        message.getRelatedItems().getCheckedRelatedItemAtIndex(2).additionalProperties.addProperty("name","microsoft word");

        return message;
    }

    @Test
    public void testConvertFromSingleItemIndexingMessage() throws Exception {
        RelatedItemIndexingMessageConverter converter = getConverter();
        RelatedItemIndexingMessage message = getIndexingMessageWithOneRelatedItem();

        RelatedItem[] products = converter.convertFrom(message);
        assertEquals("Should only have found a single product",1,products.length);

        RelatedItem product = products[0];

        assertEquals(DATE, product.getDate());
        assertEquals("1",new String(product.getId()));
        assertEquals("the related product should have 3 properties, two inherited, one for itself",3,product.getAdditionalProperties().getNumberOfProperties());

        Map<String,String> props = new HashMap<String,String>();
        product.getAdditionalProperties().convertTo(props);

        assertEquals(SITE, props.get("site"));
        assertEquals(CHANNEL,props.get("channel"));
        assertEquals(DEPARTMENT,props.get("department"));

    }

    @Test
    public void testConvertFromTwoItemsIndexingMessage() throws Exception {
        RelatedItemIndexingMessageConverter converter = getConverter();
        RelatedItemIndexingMessage message = getIndexingMessageWithTwoRelatedItem();

        RelatedItem[] products = converter.convertFrom(message);
        Arrays.sort(products, new RelatedItemComparator());

        assertEquals("Should only have found a single product",2,products.length);

        RelatedItem product1 = products[0];
        RelatedItem product2 = products[1];

        assertEquals(DATE, product1.getDate());
        assertEquals("1",new String(product1.getId()));
        assertEquals("the related product should have 3 properties, two inherited, one for itself",5,product1.getAdditionalProperties().getNumberOfProperties());

        assertEquals(DATE, product2.getDate());
        assertEquals("2",new String(product2.getId()));
        assertEquals("the related product should have 3 properties, two inherited, one for itself",5,product2.getAdditionalProperties().getNumberOfProperties());


        Map<String,String> props = new HashMap<String,String>();
        product1.getAdditionalProperties().convertTo(props);

        assertEquals(SITE,props.get("site"));
        assertEquals(CHANNEL,props.get("channel"));
        assertEquals(DEPARTMENT,props.get("department"));
        assertEquals("laptops",props.get("subcategory"));
        assertEquals("apple mac",props.get("name"));



    }

    public RelatedItemInfo createRelatedItemInfoObj(String id) {
        RelatedItemInfo info1 = new RelatedItemInfo(new SystemPropertiesConfiguration());
        info1.setId(id);
        return info1;
    }

    @Test
    public void testRelatedItemIds() {
        RelatedItemInfo info1 = createRelatedItemInfoObj("1");
        RelatedItemInfo info2 = createRelatedItemInfoObj("2");
        RelatedItemInfo info3 = createRelatedItemInfoObj("3");
        RelatedItemInfo info4 = createRelatedItemInfoObj("4");
        RelatedItemInfo info5 = createRelatedItemInfoObj("5");

        RelatedItemInfo[][] relatedItemIds = BasicRelatedItemIndexingMessageConverter.relatedIds(new RelatedItemInfo[]{info1, info2, info3, info4, info5}, 5);

        String[] concatIds = new String[relatedItemIds.length];
        for(int i = 0;i<relatedItemIds.length;i++) {
            StringBuilder b = new StringBuilder(5);
            for(int j=0;j<relatedItemIds[i].length;j++) {
                b.append(relatedItemIds[i][j].getId().toString());
            }
            concatIds[i] = b.toString();
        }

        System.out.println(Arrays.toString(concatIds));

        assertSame(info5, relatedItemIds[0][4]);
        assertSame(info1, relatedItemIds[1][4]);
        assertSame(info2, relatedItemIds[2][4]);
        assertSame(info3, relatedItemIds[3][4]);
        assertSame(info4, relatedItemIds[4][4]);

        assertEquals("12345",concatIds[0]);
        assertEquals("23451",concatIds[1]);
        assertEquals("34512",concatIds[2]);
        assertEquals("45123",concatIds[3]);
        assertEquals("51234",concatIds[4]);

    }

    @Test
    public void testConvertFromThreeProductsIndexingMessage() throws Exception {
        RelatedItemIndexingMessageConverter converter = getConverter();
        RelatedItemIndexingMessage message = getIndexingMessageWithThreeRelatedItem();

        RelatedItem[] products = converter.convertFrom(message);
        Arrays.sort(products, new RelatedItemComparator());
        assertEquals("Should only have found a single product", 3, products.length);

        RelatedItem product1 = products[0];
        RelatedItem product2 = products[1];
        RelatedItem product3 = products[2];

        assertEquals(DATE, product1.getDate());
        assertEquals("1",new String(product1.getId()));
        assertEquals("the related product should have 5 properties, two inherited, one for itself",5,product1.getAdditionalProperties().getNumberOfProperties());

        assertEquals(DATE, product2.getDate());
        assertEquals("2",new String(product2.getId()));
        assertEquals("the related product should have 5 properties, two inherited, one for itself",5,product2.getAdditionalProperties().getNumberOfProperties());

        assertEquals(DATE, product3.getDate());
        assertEquals("3",new String(product3.getId()));
        assertEquals("the related product should have 5 properties, two inherited, one for itself",5,product3.getAdditionalProperties().getNumberOfProperties());


        Map<String,String> props = new HashMap<String,String>();
        product1.getAdditionalProperties().convertTo(props);

        assertEquals(SITE,props.get("site"));
        assertEquals(CHANNEL,props.get("channel"));
        assertEquals(DEPARTMENT,props.get("department"));
        assertEquals("laptops",props.get("subcategory"));
        assertEquals("apple mac",props.get("name"));

        assertEquals(2, product1.getRelatedItemPids().length);

        String id1 = new String(product1.getRelatedItemPids()[0]);
        String id2 = new String(product1.getRelatedItemPids()[1]);

        if(id1.equals("2")) {
            assertEquals("3",id2);
        } else if(id1.equals("3")) {
            assertEquals("2",id2);
        } else {
            fail("related ids for product2 are incorrect");
        }

        props = new HashMap<String,String>();
        product2.getAdditionalProperties().convertTo(props);

        assertEquals(SITE,props.get("site"));
        assertEquals(CHANNEL,props.get("channel"));
        assertEquals(DEPARTMENT,props.get("department"));
        assertEquals(SUBCATEGORY,props.get("subcategory"));
        assertEquals("apple care insurance",props.get("name"));

        assertEquals(2, product2.getRelatedItemPids().length);

        id1 = new String(product2.getRelatedItemPids()[0]);
        id2 = new String(product2.getRelatedItemPids()[1]);

        if(id1.equals("1")) {
            assertEquals("3",id2);
        } else if(id1.equals("3")) {
            assertEquals("1",id2);
        } else {
            fail("related ids for product2 are incorrect");
        }


        props = new HashMap<String,String>();
        product3.getAdditionalProperties().convertTo(props);

        assertEquals(SITE,props.get("site"));
        assertEquals(CHANNEL,props.get("channel"));
        assertEquals(DEPARTMENT,props.get("department"));
        assertEquals(SUBCATEGORY,props.get("subcategory"));
        assertEquals("microsoft word",props.get("name"));

        assertEquals(2, product3.getRelatedItemPids().length);

        id1 = new String(product3.getRelatedItemPids()[0]);
        id2 = new String(product3.getRelatedItemPids()[1]);

        if(id1.equals("2")) {
            assertEquals("1",id2);
        } else if(id1.equals("1")) {
            assertEquals("2",id2);
        } else {
            fail("related ids for product2 are incorrect");
        }
    }

    public class RelatedItemComparator implements Comparator<RelatedItem> {

        @Override
        public int compare(RelatedItem o1, RelatedItem o2) {
            return new String(o1.getId()).compareTo(new String(o2.getId()));
        }
    }
}
