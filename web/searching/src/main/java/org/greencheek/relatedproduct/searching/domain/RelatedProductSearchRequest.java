package org.greencheek.relatedproduct.searching.domain;

import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContext;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchResponseContextHolder;
import org.greencheek.relatedproduct.util.config.Configuration;

import javax.servlet.AsyncContext;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchRequest {

//    private RelatedProductSearchType requestType;
    private final SearchResponseContextHolder requestContext = new SearchResponseContextHolder();
    private final RelatedProductSearch searchRequest;
//    private RelatedProductSearchExecutor searchExecutor;

//    private Map<String,String> requestProperties;

    public RelatedProductSearchRequest(Configuration configuration) {
        searchRequest = new RelatedProductSearch(configuration);
    }
//
//    public void setSearchExecutor(RelatedProductSearchExecutor searchExecutor) {
//        this.searchExecutor = searchExecutor;
//    }
//
//    public RelatedProductSearchExecutor getSearchExecutor() {
//        return this.searchExecutor;
//    }

    public RelatedProductSearch getSearchRequest() {
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

    public SearchResponseContextHolder getRequestContext() {
        return requestContext;
    }
//
//    public void setRequestType(RelatedProductSearchType type) {
//        this.requestType = type;
//    }
//
//    public RelatedProductSearchType getRequestType() {
//        return requestType;
//    }

//    public final static EventFactory<RelatedProductSearchRequest> FACTORY = new EventFactory<RelatedProductSearchRequest>()
//    {
//        @Override
//        public RelatedProductSearchRequest newInstance()
//        {
//            return new RelatedProductSearchRequest();
//        }
//    };

}
