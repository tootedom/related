package org.greencheek.relatedproduct.indexing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 03/06/2013
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */

/**
 * Only to be thrown when the provided index request data is malformed and cannot be
 * converted into a string representation that is parsable.
 */
public class InvalidIndexingRequestException extends RuntimeException {
    public InvalidIndexingRequestException(String message) {
        super(message);
    }

    public InvalidIndexingRequestException(Throwable error) {
        super(error);
    }

    public InvalidIndexingRequestException(String message, Throwable error) {
        super(message,error);
    }

}
