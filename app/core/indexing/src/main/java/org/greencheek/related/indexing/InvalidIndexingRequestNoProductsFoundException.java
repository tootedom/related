package org.greencheek.related.indexing;

/**
 * Represents an exception that the indexing request actually contained no products
 */
public class InvalidIndexingRequestNoProductsFoundException extends InvalidIndexingRequestException {

    public InvalidIndexingRequestNoProductsFoundException(String message) {
        super(message);
    }
}
