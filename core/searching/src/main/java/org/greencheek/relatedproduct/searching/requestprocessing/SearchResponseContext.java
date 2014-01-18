package org.greencheek.relatedproduct.searching.requestprocessing;

/**
 * Represents the class through which we can send results to the
 * user that submitted the search request.
 */
public interface SearchResponseContext<T> {

    /**
     * The type of response context that
     * we are sending results to.
     * @return
     */
    Class<T> getContextType();

    /**
     * The context, to which we will be sending results.
     * @return
     */
    T getSearchResponseContext();

    /**
     * close, in whatever way, appropriate for the held context object.
     */
    public void close();

    /**
     * creation nano timestamp; used for timing the time is takes to complete
     * requests.
     *
     * If the creation time is not supported, then return -1
     */
    public long getCreationTime();
}
