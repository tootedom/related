package org.greencheek.relatedproduct.domain;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 08/06/2013
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class RelatedProductSearchRequest {

    private final AsyncContext requestContext;
    private final Map<String,String> requestProperties;

    private RelatedProductSearchRequest(AsyncContext request, Map<String,String> requestProperties) {
        this.requestContext = request;
        this.requestProperties = new HashMap<String,String>(requestProperties);
    }

}
