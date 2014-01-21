package org.greencheek.related.searching;

/**
 * Created by dominictootell on 18/01/2014.
 */
public interface RelatedItemSearchExecutorFactory {
    RelatedItemSearchExecutor createSearchExecutor(RelatedItemSearchResultsToResponseGateway gateway);
}
