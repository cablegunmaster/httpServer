package com.jasper.model.response;

import com.jasper.model.httpenums.StatusCode;

/**
 * All variables and functions shared for the HTTP protocol
 */
public abstract class HttpResponseHandler {

    final static String LINE_END = "\r\n";
    final static String DOUBLE_LINE_END = "\r\n\r\n";
    final static String SPACE = " ";

    private String response = null;
    private StringBuilder body = new StringBuilder();
    private StatusCode statusCode = null; //status code of request.
    private double httpVersion = 1.1;
    private String websocketAccept;

    public String getWebsocketAcceptString() {
        return websocketAccept;
    }

    public void setWebsocketAcceptString(String websocketAccept) {
        this.websocketAccept = websocketAccept;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    StatusCode getStatusCode() {
        return statusCode;
    }

    public void write(String s) {
        body.append(s);
    }

    String getBody() {
        return body.toString();
    }

    public double getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(double httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void buildResponse() {
        response = toHttpResponse();
    }

    public String getResponse() {
        return response;
    }

    public abstract String toHttpResponse();
}
