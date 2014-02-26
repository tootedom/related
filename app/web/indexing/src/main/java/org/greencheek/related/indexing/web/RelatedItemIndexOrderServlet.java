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

package org.greencheek.related.indexing.web;


import org.greencheek.related.indexing.IndexRequestPublishingStatus;
import org.greencheek.related.indexing.RelatedItemIndexRequestProcessor;
import org.greencheek.related.indexing.web.bootstrap.ApplicationCtx;
import org.greencheek.related.indexing.requestprocessorfactory.IndexRequestProcessorFactory;
import org.greencheek.related.indexing.util.ResizableByteBuffer;
import org.greencheek.related.indexing.util.ResizableByteBufferNoBoundsChecking;
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
import java.io.InputStream;
import java.nio.BufferOverflowException;

/**
 *
 */
@WebServlet(urlPatterns = "/index", name="relatedPurchaseIndexOrderHandler", asyncSupported = true,loadOnStartup = 1)
public class RelatedItemIndexOrderServlet extends HttpServlet {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private IndexRequestProcessorFactory indexerFactory;
    private RelatedItemIndexRequestProcessor indexer;

    private Configuration configuration;

    private ApplicationCtx applicationCtx;

    private static final Logger log = LoggerFactory.getLogger(RelatedItemIndexOrderServlet.class);

    public void init(javax.servlet.ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
        applicationCtx = (ApplicationCtx)servletConfig.getServletContext().getAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME);

        configuration = applicationCtx.getConfiguration();
        indexerFactory = applicationCtx.getIndexRequestProcessorFactory();
        indexer = indexerFactory.createProcessor(configuration);
    }

    public void destroy() {
        indexer.shutdown();
        indexerFactory.shutdown();
        super.destroy();
    }


    protected void doPut(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {

        final AsyncContext asyncContext;
        if(request.isAsyncStarted()) {
            asyncContext = request.getAsyncContext();
        }
        else {
            asyncContext = request.startAsync(request, response);
        }

//
//            asyncContext.start(new Runnable() {
//                @Override
//                public void run() {
//                    submitRequestForProcessing(asyncContext);
//                }
//            });
        submitRequestForProcessing(asyncContext);


    }

    /**
     * Direct the doPost to doPut
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {
        this.doPut(request,response);
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
                // should never occur.  invalid content length i.e. abc, is usually
                // caught by the container.  However, a user could specify a Content-Length with
                // a value larger than an int max size
                return -1;
            }
        } else {
            return -2;
        }
    }

    private void submitRequestForProcessing(AsyncContext ctx) {
        int minPostData = configuration.getMinRelatedItemPostDataSizeInBytes();
        int maxPostData = configuration.getMaxRelatedItemPostDataSizeInBytes();
        HttpServletRequest request = (HttpServletRequest)ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse)ctx.getResponse();
        String len = request.getHeader(CONTENT_LENGTH_HEADER);
        int length = parseContentLength(maxPostData,len);

        if(length==-1) {
            response.setStatus(413);
            response.setContentLength(0);
            log.warn("Indexing Request is larger ({}) than max allowed post data: {}", len, maxPostData);
            ctx.complete();
            return;
        } else if(length == 0) {
            response.setStatus(400);
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
            response.setStatus(400);
            response.setContentLength(0);
            log.warn("Error obtaining content from request to index");
            ctx.complete();
            return;
        }

        int accumLength = 0;
        boolean canProcess = true;
        try {
            int lengthRead = 0;
            while ((lengthRead = inputStream.read(buffer)) != -1) {
                accumLength+=lengthRead;
                if(accumLength>maxPostData) {
                    response.setStatus(413);
                    response.setContentLength(0);
                    canProcess = false;
                    log.warn("Post data is larger than max allowed : {}",maxPostData);
                    break;
                }

                content.append(buffer, 0, lengthRead);

            }

            if(accumLength==0) {
                response.setStatus(400);
                response.setContentLength(0);
                canProcess = false;
                log.warn("No indexing content found in request");
            }
        } catch (BufferOverflowException e) {
            response.setStatus(413);
            response.setContentLength(0);
            canProcess = false;
            log.warn("Post data is larger than max allowed : {}",maxPostData);
        } catch (IOException exception) {
            response.setStatus(400);
            response.setContentLength(0);
            canProcess = false;
            log.warn("Error obtaining content from request to index");
        } finally {
            try {
                inputStream.close();
            } catch (IOException closeException) {

            }
        }

        try {
            if(canProcess) {
                IndexRequestPublishingStatus status = indexer.processRequest(configuration,content.toByteBuffer());

                switch(status) {
                    case FAILED:
                        response.setStatus(400);
                        response.setContentLength(0);
                        break;
                    case NO_SPACE_AVALIABLE:
                    case PUBLISHER_SHUTDOWN:
                        response.setStatus(503);
                        response.setContentLength(0);
                        break;
                    default:
                        response.setStatus(202);
                        response.setContentLength(0);
                        break;

                }
            }
        } finally {
            try {
                ctx.complete();
            } catch(IllegalStateException e) {
                log.warn("Unable to complete the async context.  A timeout more than likely occurred");
            }

        }

    }

}
