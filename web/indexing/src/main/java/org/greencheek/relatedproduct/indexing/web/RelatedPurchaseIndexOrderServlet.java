package org.greencheek.relatedproduct.indexing.web;


import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.greencheek.relatedproduct.indexing.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.indexing.util.ResizableByteBuffer;
import org.greencheek.relatedproduct.indexing.util.ResizableByteBufferNoBoundsChecking;
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
import java.io.InputStream;

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
     * @param maxPostDataSize
     * @return -1 if size of post data is too large, or corrupt, -2 if we don't have content length (chunked),
     *         or the size (this could be 0)
     */
    private int parseContentLength(int maxPostDataSize, String length) {
        if(length!=null) {
            try {
                int size = Integer.parseInt(length);
                if(size>maxPostDataSize) return -1;
                else return size;
            } catch(NumberFormatException e) {
                return -1;
            }
        } else {
            return -2;
        }
    }

    private void submitRequestForProcessing(AsyncContext ctx) {
        int minPostData = configuration.getMinRelatedProductPostDataSizeInBytes();
        int maxPostData = configuration.getMaxRelatedProductPostDataSizeInBytes();
        HttpServletRequest request = (HttpServletRequest)ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse)ctx.getResponse();
        int length = parseContentLength(maxPostData,request.getHeader(CONTENT_LENGTH_HEADER));

        if(length==-1) {
            response.setStatus(413);
            response.setContentLength(0);
            log.warn("Post data is larger than max allowed : {}",maxPostData);
            ctx.complete();
            return;
        } else if(length == 0) {
            response.setStatus(413);
            response.setContentLength(0);
            log.warn("No post data.");
            ctx.complete();
            return;
        }


        ResizableByteBuffer content;
        byte[] buffer;

        if(length>0) {
            content = new ResizableByteBufferNoBoundsChecking(length,length);
            buffer = new byte[length];
        } else {
            content = new ResizableByteBufferNoBoundsChecking(minPostData,maxPostData);
            buffer = new byte[minPostData];
        }

        InputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException exception) {
            response.setStatus(413);
            response.setContentLength(0);
            log.warn("Error obtaining content from request to index");
            ctx.complete();
            return;
        }

        int lengthRead = 0;
        int accumLength = 0;
        boolean canProcess = true;
        try {
            while ((lengthRead = inputStream.read(buffer)) != -1) {
                content.append(buffer, 0, lengthRead);
                accumLength+=lengthRead;
                if(accumLength>maxPostData) {
                    response.setStatus(413);
                    canProcess = false;
                    log.warn("Post data is larger than max allowed : {}",maxPostData);
                    break;

                }
            }
            if(canProcess) {
                response.setStatus(202);
                response.setContentLength(0);
            }

        } catch (IOException exception) {
            log.warn("Error obtaining content from request to index");
            ctx.complete();
            return;
        } finally {
            try {
                inputStream.close();
            } catch (IOException closeException) {

            }
        }

        try {
            if(canProcess) {
                if(content.size()>0);
                    indexer.processRequest(configuration,content.toByteBuffer());
            }
        } finally {
            ctx.complete();
        }

    }

}
