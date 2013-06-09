package org.greencheek.relatedproduct.searching;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;

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
public interface RelatedProductSearchExecutor {

    public void executeSearch(RelatedProductSearch searchRequest);
}
