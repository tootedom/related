package org.greencheek.related.indexing;

/**
 * Base exception for the parsing of request data into a
 * {@link org.greencheek.related.api.indexing.RelatedItemIndexingMessage}
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
