package org.greencheek.relatedproduct.api;

import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.util.config.SystemPropertiesConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RelatedProductInfoIdentifierTest {

    @Test
    public void testIdTruncation() {
        System.setProperty("related-product.related.product.id.length","10");
        Configuration c = new SystemPropertiesConfiguration();
        RelatedProductInfoIdentifier id = new RelatedProductInfoIdentifier(c);

        id.setId("1234567891");
        assertEquals("1234567891",id.toString());

        id.setId("12345678910");
        assertEquals("1234567891",id.toString());
    }

    @Test
    public void testIdDuplication() {
        System.setProperty("related-product.related.product.id.length","10");
        Configuration c = new SystemPropertiesConfiguration();
        RelatedProductInfoIdentifier id = new RelatedProductInfoIdentifier(c);

        id.setId("12345678910123456789");

        char[] duplicatedChars = id.duplicate();

        assertEquals(10,duplicatedChars.length);
        assertEquals("1234567891",new String(duplicatedChars));

        duplicatedChars[0]  = 'p';
        assertEquals("1234567891",id.toString());

        id.getIdCharArray()[0] = 'p';

        assertEquals("p234567891",id.toString());


    }
}
