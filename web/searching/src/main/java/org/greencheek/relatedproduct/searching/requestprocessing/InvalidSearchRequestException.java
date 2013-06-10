package org.greencheek.relatedproduct.searching.requestprocessing;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
public class InvalidSearchRequestException extends RuntimeException {
    public InvalidSearchRequestException(String message) {
        super(message);
    }

    public InvalidSearchRequestException(Throwable error) {
        super(error);
    }

    public InvalidSearchRequestException(String message, Throwable error) {
        super(message,error);
    }

}
