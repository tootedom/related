package org.greencheek.relatedproduct.indexing.web;


import org.elasticsearch.common.netty.buffer.DynamicChannelBuffer;
import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * *
 */
@WebServlet(urlPatterns = "/index", name="relatedPurchaseIndexOrderHandler", asyncSupported = true,loadOnStartup = 1)
public class RelatedPurchaseIndexOrderServlet extends HttpServlet {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private RelatedProductIndexRequestProcessor indexer;

    private Configuration configuration;

    private ApplicationCtx applicationCtx;

    private static final Logger log = LoggerFactory.getLogger(RelatedPurchaseIndexOrderServlet.class);

    public void init(javax.servlet.ServletConfig servletConfig) throws javax.servlet.ServletException
    {
        super.init(servletConfig);
        applicationCtx = (ApplicationCtx)servletConfig.getServletContext().getAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME);

        configuration = applicationCtx.getConfiguration();
        indexer = applicationCtx.getIndexRequestProcessor();
    }

    public void destroy() {
        super.destroy();
        indexer.shutdown();
    }



    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {

        final AsyncContext asyncContext;
        if(request.isAsyncStarted()) {
            asyncContext = request.getAsyncContext();
        }
        else {
            asyncContext = request.startAsync(request, response);
        }
        asyncContext.setTimeout(10000*2);

//        try {
//            asyncContext.start(new Runnable() {
//                @Override
//                public void run() {
        submitRequestForProcessing(asyncContext);
//                }
//            });
//        } catch(Exception e) {
//
//
//        }


    }

    /**
     * If there is a content length header.  Check the reported length, making sure
     * it isn't over the max post data size.
     *
     * @param request
     * @param maxPostDataSize
     * @return true if content length is ok, or if there is no content length.  false if content length is present and
     * exceeds allowed size
     */
    private boolean isContentLengthOk(HttpServletRequest request, int maxPostDataSize) {
        String length = request.getHeader("CONTENT_LENGTH_HEADER");
        if(length!=null) {
            try {
                int size = Integer.parseInt(length);
                if(size>maxPostDataSize) return false;
                else return true;
            } catch(NumberFormatException e) {
                return true;
            }
        } else {
            return true;
        }
    }

    private void submitRequestForProcessing(AsyncContext ctx) {
        int minPostData = configuration.getMinRelatedProductPostDataSizeInBytes();
        int maxPostData = configuration.getMaxRelatedProductPostDataSizeInBytes();
        HttpServletRequest request = (HttpServletRequest)ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse)ctx.getResponse();

        if(!isContentLengthOk(request,maxPostData)) {
            response.setStatus(413);
            log.warn("Post data is larger than max allowed : {}",maxPostData);
            ctx.complete();
            return;
        }

        DynamicChannelBuffer channel = new DynamicChannelBuffer(minPostData);
        byte[] buffer = new byte[minPostData];
        InputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException exception) {
            log.warn("Error obtaining content from request to index");
            return;
        }

        int length = 0;
        int accumLength = 0;
        boolean canProcess = true;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                channel.writeBytes(buffer,0,length);
                accumLength+=length;
                if(accumLength>maxPostData) {
                    response.setStatus(413);
                    canProcess = false;
                    log.warn("Post data is larger than max allowed : {}",maxPostData);
                    break;

                }
            }
            if(canProcess) {
                response.setStatus(202);
            }

        } catch (IOException exception) {
            log.warn("Error obtaining content from request to index");
            return;
        } finally {
            try {
                inputStream.close();
            } catch (IOException closeException) {

            }
        }

        if(canProcess) {
            indexer.processRequest(configuration,channel.toByteBuffer().array());
        }

        ctx.complete();

    }

}
