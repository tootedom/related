package org.greencheek.relatedproduct.indexing;

/**
 * Only to be thrown when the provided index request data is malformed and cannot be
 * converted into a string representation that is parsable.
 */
public class InvalidIndexingRequestParsingException extends InvalidIndexingRequestException {
    public InvalidIndexingRequestParsingException(Exception cause) {
        super(cause);
    }
}
