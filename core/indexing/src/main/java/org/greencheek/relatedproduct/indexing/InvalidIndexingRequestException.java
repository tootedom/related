package org.greencheek.relatedproduct.indexing;

/**
 * Base exception for the parsing of request data into a
 * {@link org.greencheek.relatedproduct.api.indexing.RelatedProductIndexingMessage}
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
