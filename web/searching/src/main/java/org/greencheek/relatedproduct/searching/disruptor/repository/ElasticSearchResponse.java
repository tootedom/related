package org.greencheek.relatedproduct.searching.disruptor.repository;

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

    public ElasticSearchResponse(String exception) {
        this.exception = exception;
        this.failure=true;
        this.searchResponse = null;
    }

    public ElasticSearchResponse(SearchResponse response) {
        this.failure=false;
        this.exception ="";
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
