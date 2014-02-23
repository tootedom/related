package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import org.greencheek.related.elastic.http.HttpSearchExecutionStatus;
import org.greencheek.related.elastic.http.HttpMethod;
import org.greencheek.related.elastic.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

/**
 *
 * Created by dominictootell on 17/02/2014.
 */
public  class AHCRequestExecutor {
    private static final Logger log = LoggerFactory.getLogger(AHCRequestExecutor.class);

    /**
     * Executes a blocking request.
     *
     * @param client
     * @param method
     * @param host
     * @param path
     * @param searchQuery
     * @return
     */
    public static HttpResult executeSearch(AsyncHttpClient client, HttpMethod method, String host,
                                     String path, String searchQuery) {
        RequestBuilder requestBuilder = new RequestBuilder(method.name());

        log.debug("Executing request against host {} with path {}",host,path);
        if(searchQuery!=null) {
            requestBuilder.setBody(searchQuery);
        }

        requestBuilder.setUrl(host + path);
        try {
            Response res = client.executeRequest(requestBuilder.build(),new AsyncCompletionHandlerBase()).get();
            return new HttpResult(HttpSearchExecutionStatus.OK,res.getResponseBody());
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause!=null) {
                if(cause instanceof ConnectException)
                {
                    log.error("Unable to connect to {}",host,e);
                    return HttpResult.CONNECTION_FAILURE;
                }
                else if (cause instanceof TimeoutException) {
                    log.error("Request timeout talking to {}",host,e);
                    return HttpResult.REQUEST_TIMEOUT_FAILURE;
                }
                else if (cause instanceof IOException && cause.getMessage().equalsIgnoreCase("closed")) {
                    log.warn("Unable to use client, client is closed");
                    return HttpResult.CLIENT_CLOSED;
                }
                else {
                    log.error("Exception talking to {}",host,e);
                    return new HttpResult(HttpSearchExecutionStatus.REQUEST_FAILURE,null);
                }
            }
            else {
                log.error("Exception talking to {}",host,e);
                return new HttpResult(HttpSearchExecutionStatus.REQUEST_FAILURE,null);
            }
        }
    }
}
