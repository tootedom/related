package org.greencheek.relatedproduct.searching.requestprocessing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public class ValidationMessageTest {

    @Test
    public void testValidationMessageCreation() {
        ValidationMessage validMessage = new ValidationMessage(true,"id","all good");
        ValidationMessage invalidMessage = new ValidationMessage(false,"bob","all bad");

        assertTrue(validMessage.isValid());
        assertFalse(invalidMessage.isValid());

        assertEquals("property should be 'id'","id",validMessage.getInvalidProperty());
        assertEquals("property should be 'bob'","bob",invalidMessage.getInvalidProperty());
        assertEquals("Error message should be 'all good","all good",validMessage.getInvalidMessage());
        assertEquals("Error message should be 'all bad","all bad",invalidMessage.getInvalidMessage());



    }
}
