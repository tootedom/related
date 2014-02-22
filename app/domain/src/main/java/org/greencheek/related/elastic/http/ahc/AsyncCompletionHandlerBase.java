package org.greencheek.related.elastic.http.ahc;

import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copy of the AsyncCompletionHandlerBase to avoid the non static lookup of the logger.
 */
public class AsyncCompletionHandlerBase implements AsyncHandler<Response>, ProgressAsyncHandler<Response> {
    private final static Logger log = LoggerFactory.getLogger(AsyncCompletionHandlerBase.class);
    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();

    /**
     * {@inheritDoc}
     */
    public Response onCompleted(Response response) throws Exception {
        return response;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public void onThrowable(Throwable t) {
        log.debug(t.getMessage(), t);
    }

    /**
     * {@inheritDoc}
     */
    public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
        builder.accumulate(content);
        return STATE.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    public STATE onStatusReceived(final HttpResponseStatus status) throws Exception {
        builder.reset();
        builder.accumulate(status);
        return STATE.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
        builder.accumulate(headers);
        return STATE.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    public final Response onCompleted() throws Exception {
        return onCompleted(builder.build());
    }


    /**
     * Invoked when the content (a {@link java.io.File}, {@link String} or {@link java.io.FileInputStream} has been fully
     * written on the I/O socket.
     *
     * @return a {@link com.ning.http.client.AsyncHandler.STATE} telling to CONTINUE or ABORT the current processing.
     */
    public STATE onHeaderWriteCompleted() {
        return STATE.CONTINUE;
    }

    /**
     * Invoked when the content (a {@link java.io.File}, {@link String} or {@link java.io.FileInputStream} has been fully
     * written on the I/O socket.
     *
     * @return a {@link com.ning.http.client.AsyncHandler.STATE} telling to CONTINUE or ABORT the current processing.
     */
    public STATE onContentWriteCompleted() {
        return STATE.CONTINUE;
    }

    /**
     * Invoked when the I/O operation associated with the {@link Request} body as been progressed.
     *
     * @param amount  The amount of bytes to transfer.
     * @param current The amount of bytes transferred
     * @param total   The total number of bytes transferred
     * @return a {@link com.ning.http.client.AsyncHandler.STATE} telling to CONTINUE or ABORT the current processing.
     */
    public STATE onContentWriteProgress(long amount, long current, long total) {
        return STATE.CONTINUE;
    }
}
