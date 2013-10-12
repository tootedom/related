package org.greencheek.relatedproduct.domain.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 11:57
 *
 * Allow for future different implmentations of the SearchRequestLookupKey
 * by creating a Factory that creates the direct implementations.  So we can
 * swap in and out the implementations rather than having a direct dependency on
 * a particular implementation
 */
public interface SearchRequestLookupKeyFactory {

    SearchRequestLookupKey createSearchRequestLookupKey(String key);
}
