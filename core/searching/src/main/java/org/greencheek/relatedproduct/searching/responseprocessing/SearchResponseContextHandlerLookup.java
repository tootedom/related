package org.greencheek.relatedproduct.searching.responseprocessing;


/**
 * Maps a Class to a SearchResponseContextHandler, that is responsible for dealing with
 * sending {@link org.greencheek.relatedproduct.searching.domain.api.SearchResultsEvent} to that
 * Class type.
 */
public interface SearchResponseContextHandlerLookup {
    public SearchResponseContextHandler getHandler(Class responseClassToHandle);
}
