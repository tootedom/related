package org.greencheek.related.api.searching.lookup;


/**
 * See:
 * https://devcentral.f5.com/weblogs/david/archive/2012/01/20/hashdos-ndash-the-post-of-doom-explained.aspx
 *
 *
 * A string that is going to be used as a lookup for an async context within a map.
 * This is performed so that if more that one request hits the server, and a search is currently
 * being performed for the same data, upon return we can obtain all the requests waiting for that
 * search request, and reply to them all using the same result; rather than having to perform
 * the same request.
 *
 * The search request lookup key, is made up of the url parameters that are passed directly to the
 * search endpoint.  Therefore as the mashed up string of url parameters will be used in a hash map,
 * we need to use a different hash code than that of the standard jdk hash code.
 *
 * The implementation must implement the hashCode and equals method, that are protected against
 * a high number of collisions as per the reference above and CVE-2012-0022
 * http://stackoverflow.com/questions/8669946/application-vulnerability-due-to-non-random-hash-functions
 *
 */
public interface SearchRequestLookupKey {

    public boolean equals(Object o);

    public int hashCode();

    public String toString();

}
