package org.greencheek.related.searching.domain;

import org.greencheek.related.api.searching.RelatedItemSearch;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.util.config.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class RelatedItemSearchRequest {

    private SearchResponseContext[] requestContexts;
    private final RelatedItemSearch searchRequest;


    public RelatedItemSearchRequest(Configuration configuration) {
        searchRequest = new RelatedItemSearch(configuration);
    }


    public RelatedItemSearch getSearchRequest() {
        return this.searchRequest;
    }


    public SearchResponseContext[] getRequestContexts() {
        return requestContexts;
    }
    public void setRequestContexts(SearchResponseContext[] contexts) { this.requestContexts = contexts; }

}
