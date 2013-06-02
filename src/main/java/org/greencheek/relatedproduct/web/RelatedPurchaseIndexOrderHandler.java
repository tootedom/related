package org.greencheek.relatedproduct.web;

import org.greencheek.relatedproduct.indexing.RelatedProductIndexRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


//@Service(value = "relatedPurchaseIndexOrderHandler")
public class RelatedPurchaseIndexOrderHandler  {

	private static final Logger log = LoggerFactory.getLogger(RelatedPurchaseIndexOrderHandler.class);

    @Inject
    private RelatedProductIndexRequestProcessor indexer;

    public RelatedPurchaseIndexOrderHandler() {


    }

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Serving: {}", request.getRequestURI());

        final AsyncContext asyncContext;
        if(request.isAsyncStarted()) {
            asyncContext = request.getAsyncContext();
        }
        else {
		    asyncContext = request.startAsync(request, response);
        }
		asyncContext.setTimeout(1000*2);
		final HttpRequestTask task = new HttpRequestTask(asyncContext);
		asyncContext.addListener(task);

        try {
            submitRequestForProcessing(request);
        } finally {
            response.setStatus(201);
            asyncContext.complete();
        }
	}

    private void submitRequestForProcessing(HttpServletRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        InputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException exception) {
            log.warn("Error obtaining content from request to index");
           return;
        }
        int length = 0;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
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

        indexer.processRequest(baos.toByteArray());

    }

	private class HttpRequestTask implements AsyncListener {

		private final AsyncContext ctx;

		public HttpRequestTask(AsyncContext ctx) throws IOException {
			this.ctx = ctx;
		}

		@Override
		public void onComplete(AsyncEvent asyncEvent) throws IOException {
			log.debug("Asychronous index request handled.");
		}

		@Override
		public void onTimeout(AsyncEvent asyncEvent) throws IOException {
			log.warn("Asynchronous index request timeout.");
		}

		@Override
		public void onError(AsyncEvent asyncEvent) throws IOException {
			log.warn("Asynchronous index request error.");
		}

		@Override
		public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
		}
	}

}
