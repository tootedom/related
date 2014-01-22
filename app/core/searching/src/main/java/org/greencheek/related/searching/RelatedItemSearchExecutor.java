package org.greencheek.related.searching;

import org.greencheek.related.api.searching.RelatedItemSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */

/**
 * Searching
 */
public interface RelatedItemSearchExecutor {

    public void executeSearch(RelatedItemSearch searchRequest);
    public void shutdown();
}
