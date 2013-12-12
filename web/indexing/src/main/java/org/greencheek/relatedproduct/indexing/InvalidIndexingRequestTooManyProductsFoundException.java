package org.greencheek.relatedproduct.indexing;

/**
 * When the related content message has too many related products
 */
public class InvalidIndexingRequestTooManyProductsFoundException extends InvalidIndexingRequestException {

    public InvalidIndexingRequestTooManyProductsFoundException(String message) {
        super(message);
    }
}
