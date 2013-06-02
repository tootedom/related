package org.greencheek.relatedproduct.indexing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 01/06/2013
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
public class InvalidRelatedProductJsonException extends RuntimeException {
    public InvalidRelatedProductJsonException(Exception cause) {
        super(cause);
    }
}
