package org.greencheek.related.elastic.http;

public class HttpResult {
    public static final HttpResult CONNECTION_FAILURE = new HttpResult(HttpSearchExecutionStatus.CONNECTION_FAILURE,"");
    public static final HttpResult REQUEST_TIMEOUT_FAILURE = new HttpResult(HttpSearchExecutionStatus.REQUEST_TIMEOUT,"");
    public static final HttpResult REQUEST_FAILURE = new HttpResult(HttpSearchExecutionStatus.REQUEST_FAILURE,"");
    public static final HttpResult CLIENT_CLOSED = new HttpResult(HttpSearchExecutionStatus.CLIENT_CLOSED,"");


    private final HttpSearchExecutionStatus status;
    private final String res;

    public HttpResult(HttpSearchExecutionStatus status, String result) {
        this.status = status;
        this.res = result;
    }

    public HttpSearchExecutionStatus getStatus() {
        return status;
    }

    public String getResult() {
        return res;
    }

}