package org.greencheek.relatedproduct.api.searching.lookup;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedProductSearchLookupKeyGenerator {

    /**
     * Given a RelatedProductSearch object, a SearchRequestLookup key is generated.
     * the generated key is created from the properties that have been set on that RelatedProductSearch
     * object.
     *
     * @param userSearch The user search.
     * @return the SearchRequestLookupKey that represents that userSearch.
     */
    SearchRequestLookupKey createSearchRequestLookupKeyFor(RelatedProductSearch userSearch);


    /**
     * Given a RelatedProductSearch object, a SearchRequestLookup key is generated.
     * the generated key is created from the properties that have been set on that RelatedProductSearch
     * object.
     *
     * This method has the side effect of modifying the RelatedProductSearch object to set it's lookupKey (
     * @see RelatedProductSearch#setLookupKey(org.greencheek.relatedproduct.api.searching.lookup.SearchRequestLookupKey)
     *
     * @param userSearch The user search.
     * @return the SearchRequestLookupKey that represents that userSearch.
     */
    void setSearchRequestLookupKeyOn(RelatedProductSearch userSearch);
}
