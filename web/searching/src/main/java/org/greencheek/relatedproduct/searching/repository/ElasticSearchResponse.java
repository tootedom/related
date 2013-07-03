package org.greencheek.relatedproduct.searching.repository;

import org.elasticsearch.action.search.SearchResponse;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchResponse {

    private final String exception;
    private final boolean failure;


    private final SearchResponse searchResponse;

    public ElasticSearchResponse(SearchResponse response,String exception,boolean failure) {
        this.exception = exception;
        this.failure=failure;
        this.searchResponse = response;
    }


    public String getException() {
        return exception;
    }

    public boolean isFailure() {
        return failure;
    }

    public SearchResponse getSearchResponse() {
        return searchResponse;
    }

}
