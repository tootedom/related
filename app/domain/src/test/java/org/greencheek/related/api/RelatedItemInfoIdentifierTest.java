package org.greencheek.related.api;

import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;
import org.greencheek.related.util.config.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RelatedItemInfoIdentifierTest {

    @After
    public void tearDown() {
        System.clearProperty(ConfigurationConstants.PROPNAME_RELATED_ITEM_ID_LENGTH);
    }

    @Test
    public void testIdTruncation() {
        System.setProperty(ConfigurationConstants.PROPNAME_RELATED_ITEM_ID_LENGTH,"10");
        Configuration c = new SystemPropertiesConfiguration();
        RelatedItemInfoIdentifier id = new RelatedItemInfoIdentifier(c);

        id.setId("1234567891");
        assertEquals("1234567891",id.toString());

        id.setId("12345678910");
        assertEquals("1234567891",id.toString());
    }

    @Test
    public void testIdDuplication() {
        System.setProperty(ConfigurationConstants.PROPNAME_RELATED_ITEM_ID_LENGTH,"10");
        Configuration c = new SystemPropertiesConfiguration();
        RelatedItemInfoIdentifier id = new RelatedItemInfoIdentifier(c);

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
