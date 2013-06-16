package org.greencheek.relatedproduct.web;


import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.InvalidSearchRequestException;
import org.greencheek.relatedproduct.util.config.Configuration;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * *
 */
@WebServlet(urlPatterns = "/frequentlyrelatedto/*", name="relatedPurchaseIndexOrderHandler",
        asyncSupported = true, loadOnStartup = 1)
public class RelatedProductSearchServlet extends HttpServlet {

    private static final Pattern ID_PATTERN = Pattern.compile(".*/([^/?]+)");

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private RelatedProductSearchRequestProcessor productSearchRequestProcessor;
    private Configuration configuration;

    private ApplicationCtx applicationCtx;

    private static final Logger log = LoggerFactory.getLogger(RelatedProductSearchServlet.class);

    public void init(javax.servlet.ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
        applicationCtx = (ApplicationCtx)servletConfig.getServletContext().getAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME);

        configuration = applicationCtx.getConfiguration();
        productSearchRequestProcessor = applicationCtx.getRequestProcessor();
    }


    public void destroy() {
        super.destroy();
        productSearchRequestProcessor.shutdown();
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
        asyncContext.setTimeout(30000);


        try {
            asyncContext.start(new Runnable() {
                @Override
                public void run() {
                    try {
                        submitRequestForProcessing(asyncContext, request);
                    } catch(Exception e) {
                        log.warn("Exception submitting request for processing",e);
                        asyncContext.complete();
                    }
                }
            });
        } catch(Exception e) {


        }

    }

    private String getId(String path) {
        log.debug("obtaining id from endpoint {}",path);
        Matcher endpointMatcher = ID_PATTERN.matcher(path);
        if(endpointMatcher.find()) {
            return endpointMatcher.group(1);
        } else return null;
    }

    private void submitRequestForProcessing(AsyncContext ctx, HttpServletRequest request) {
        Map<String,String> params = convertToFirstParameterValueMap(request.getParameterMap());
        params.put(configuration.getRequestParameterForId(), getId(request.getPathInfo()));
        try {
            productSearchRequestProcessor.processRequest(RelatedProductSearchType.FREQUENTLY_RELATED_WITH,params,ctx);
        } catch (InvalidSearchRequestException invalidRequestException) {
            log.warn("Invalid search request",invalidRequestException);
            ctx.complete();
        }
    }

    private Map<String,String> convertToFirstParameterValueMap(Map<String,String[]> params) {
        Map<String,String> parms = new HashMap<String,String>(params.size());
        for(String key : params.keySet()) {
            String[] values = params.get(key);
            if(values!=null && values.length>0) {
                parms.put(key,values[0]);
            }
        }
        return parms;
    }


}
