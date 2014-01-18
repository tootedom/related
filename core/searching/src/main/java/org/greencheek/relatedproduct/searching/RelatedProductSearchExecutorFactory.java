package org.greencheek.relatedproduct.searching;

/**
 * Created by dominictootell on 18/01/2014.
 */
public interface RelatedProductSearchExecutorFactory {
    RelatedProductSearchExecutor createSearchExecutor(RelatedProductSearchResultsToResponseGateway gateway);
}
