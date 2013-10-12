package org.greencheek.relatedproduct.domain.searching;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 12/10/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
public class SipHashSearchRequestLookupKeyFactory implements SearchRequestLookupKeyFactory {
    @Override
    public SearchRequestLookupKey createSearchRequestLookupKey(String key) {
        return new SipHashSearchRequestLookupKey(key);
    }
}
