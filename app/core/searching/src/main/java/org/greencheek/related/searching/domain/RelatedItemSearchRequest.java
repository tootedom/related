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

//    private RelatedItemSearchType requestType;
    private SearchResponseContext[] requestContexts;
    private final RelatedItemSearch searchRequest;
//    private RelatedItemSearchExecutor searchExecutor;

//    private Map<String,String> requestProperties;

    public RelatedItemSearchRequest(Configuration configuration) {
        searchRequest = new RelatedItemSearch(configuration);
    }
//
//    public void setSearchExecutor(RelatedItemSearchExecutor searchExecutor) {
//        this.searchExecutor = searchExecutor;
//    }
//
//    public RelatedItemSearchExecutor getSearchExecutor() {
//        return this.searchExecutor;
//    }

    public RelatedItemSearch getSearchRequest() {
        return this.searchRequest;
    }

//    public void setRequestProperties(Map<String,String> requestProperties) {
//        this.requestProperties = requestProperties;
//    }
//
//    public Map<String, String> getRequestProperties() {
//        return requestProperties;
//    }

//    public void setRequestContext(SearchResponseContextHolder clientCtx) {
//        this.requestContext = clientCtx;
//    }

    public SearchResponseContext[] getRequestContexts() {
        return requestContexts;
    }
    public void setRequestContexts(SearchResponseContext[] contexts) { this.requestContexts = contexts; }
//
//    public void setRequestType(RelatedItemSearchType type) {
//        this.requestType = type;
//    }
//
//    public RelatedItemSearchType getRequestType() {
//        return requestType;
//    }

//    public final static EventFactory<RelatedItemSearchRequest> FACTORY = new EventFactory<RelatedItemSearchRequest>()
//    {
//        @Override
//        public RelatedItemSearchRequest newInstance()
//        {
//            return new RelatedItemSearchRequest();
//        }
//    };

}
