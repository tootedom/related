package org.greencheek.related.api.searching.lookup;

import org.greencheek.related.api.searching.RelatedItemSearch;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 13/10/2013
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public interface RelatedItemSearchLookupKeyGenerator {

    /**
     * Given a RelatedItemSearch object, a SearchRequestLookup key is generated.
     * the generated key is created from the properties that have been set on that RelatedItemSearch
     * object.
     *
     * @param userSearch The user search.
     * @return the SearchRequestLookupKey that represents that userSearch.
     */
    SearchRequestLookupKey createSearchRequestLookupKeyFor(RelatedItemSearch userSearch);


    /**
     * Given a RelatedItemSearch object, a SearchRequestLookup key is generated.
     * the generated key is created from the properties that have been set on that RelatedItemSearch
     * object.
     *
     * This method has the side effect of modifying the RelatedItemSearch object to set it's lookupKey (
     * @see org.greencheek.related.api.searching.RelatedItemSearch#setLookupKey(org.greencheek.related.api.searching.lookup.SearchRequestLookupKey)
     *
     * @param userSearch The user search.
     * @return the SearchRequestLookupKey that represents that userSearch.
     */
    void setSearchRequestLookupKeyOn(RelatedItemSearch userSearch);
}
