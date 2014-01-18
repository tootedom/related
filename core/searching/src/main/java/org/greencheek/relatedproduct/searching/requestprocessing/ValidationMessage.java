package org.greencheek.relatedproduct.searching.requestprocessing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 23:18
 * To change this template use File | Settings | File Templates.
 */
public class ValidationMessage {
    private final boolean isValid;
    private final String invalidMessage;
    private final String invalidProperty;

    public ValidationMessage(boolean valid,String property, String message)
    {
        isValid = valid;
        invalidProperty = property;
        invalidMessage = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public String getInvalidProperty() {
        return invalidProperty;
    }
}
