package org.greencheek.relatedproduct.searching.requestprocessing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 23:18
 * To change this template use File | Settings | File Templates.
 */
public class ValidationMessage {
    public final boolean isValid;
    public final String invalidMessage;
    public final String invalidProperty;

    public ValidationMessage(boolean valid,String property, String message)
    {
        isValid = valid;
        invalidProperty = property;
        invalidMessage = message;
    }
}
