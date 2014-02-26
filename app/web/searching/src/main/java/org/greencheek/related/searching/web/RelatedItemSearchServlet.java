/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.searching.web;


import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.RelatedItemSearchRequestProcessor;
import org.greencheek.related.searching.disruptor.requestprocessing.SearchRequestSubmissionStatus;
import org.greencheek.related.searching.requestprocessing.AsyncServletSearchResponseContext;
import org.greencheek.related.searching.requestprocessing.LogDebuggingSearchResponseContext;
import org.greencheek.related.searching.requestprocessing.SearchResponseContext;
import org.greencheek.related.searching.web.bootstrap.ApplicationCtx;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Servlet responsible for taking search request for related products and submitting them to the backend for
 * processing.
 *
 * uses servlet 3
 *
 */
@WebServlet(urlPatterns = "/frequentlyrelatedto/*", name="relatedPurchaseIndexOrderHandler",
        asyncSupported = true, loadOnStartup = 1)
public class RelatedItemSearchServlet extends HttpServlet {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private RelatedItemSearchRequestProcessor productSearchRequestProcessor;
    private Configuration configuration;

    private ApplicationCtx applicationCtx;

    private static final Logger log = LoggerFactory.getLogger(RelatedItemSearchServlet.class);

    public void init(javax.servlet.ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
        applicationCtx = (ApplicationCtx)servletConfig.getServletContext().getAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME);
        configuration = applicationCtx.getConfiguration();
        productSearchRequestProcessor = applicationCtx.getRequestProcessor();
    }


    public void destroy() {
        productSearchRequestProcessor.shutdown();
        super.destroy();
    }

    protected void doGet(final HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {

        final AsyncContext asyncContext;
        if(request.isAsyncStarted()) {
            asyncContext = request.getAsyncContext();
        }
        else {
            asyncContext = request.startAsync(request, response);
        }
        asyncContext.setTimeout(configuration.getHttpAsyncSearchingRequestTimeout());

        try {
            submitRequestForProcessing(asyncContext, request);
        } catch (Exception e) {
            log.warn("Exception submitting request for processing", e);
            asyncContext.complete();
        }


    }

    // Returns the last part of the url path.  i.e. the bit after the last slash '/', or empty
    // string
    private static String getId(String path) {
        log.debug("obtaining id from endpoint {}",path);
        if(path==null) return "";
        int index = path.lastIndexOf('/');
        return (index==-1) ? "" : path.substring(index+1);
    }

    private void submitRequestForProcessing(AsyncContext ctx, HttpServletRequest request) {
        Map<String,String> params = convertToFirstParameterValueMap(request.getParameterMap());
        params.put(configuration.getRequestParameterForId(), getId(request.getPathInfo()));
        log.debug("Request {}", params);
        SearchResponseContext[] contexts;
        if (configuration.isSearchResponseDebugOutputEnabled()) {
            contexts = new SearchResponseContext[]{
                    new AsyncServletSearchResponseContext(ctx),
                    LogDebuggingSearchResponseContext.INSTANCE
            };

        } else {
            contexts = new SearchResponseContext[]{
                    new AsyncServletSearchResponseContext(ctx)
            };
        }
        SearchRequestSubmissionStatus status = productSearchRequestProcessor.processRequest(RelatedItemSearchType.FREQUENTLY_RELATED_WITH, params, contexts);

        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        boolean completeRequest = false;
        switch (status) {
            case PROCESSING_REJECTED_AT_MAX_CAPACITY:
                response.setStatus(503);
                response.setContentLength(0);
                completeRequest = true;
                break;
            case REQUEST_VALIDATION_FAILURE:
                response.setStatus(400);
                response.setContentLength(0);
                completeRequest = true;
                break;
            case PROCESSING:
                completeRequest = false;
        }

        if(completeRequest) {
            ctx.complete();
        }

    }

    /**
     *
     * @param params
     * @return
     */
    private Map<String,String> convertToFirstParameterValueMap(Map<String,String[]> params) {
        Map<String,String> parms = new HashMap<String,String>((int)Math.ceil(params.size()+1/0.75));
        for(String key : params.keySet()) {
            String[] values = params.get(key);
            if(values!=null && values.length>0) {
                parms.put(key,values[0]);
            }
        }
        return parms;
    }


}
