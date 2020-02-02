package com.jasper.model.http;

import com.jasper.model.http.enums.StatusCode;

import java.util.HashMap;
import java.util.Map;

import static com.jasper.model.http.enums.StatusCode.NOT_FOUND;

/**
 * All variables and functions shared for the HTTP protocol
 */
public abstract class HttpResponseHandler {

    protected final static String LINE_END = "\r\n";
    protected final static String SPACE = " ";
    final static String DOUBLE_LINE_END = "\r\n\r\n";

    private StatusCode statusCode = NOT_FOUND; //status code of request.
    private StringBuilder body = new StringBuilder();
    private String httpVersion;
    private String webSocketAccept;
    private String contentType = "text/html; charset=utf-8";
    private Map<String, String> headers = new HashMap<>();

    protected String getWebsocketAcceptString() {
        return webSocketAccept;
    }

    public void setWebsocketAcceptString(String websocketAccept) {
        this.webSocketAccept = websocketAccept;
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

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    protected StringBuilder addHeaders() {
        StringBuilder response = new StringBuilder();
        for (String key : headers.keySet()) {
            response.append(key)
                    .append(":")
                    .append(headers.get(key))
                    .append(LINE_END);
        }
        return response;
    }

    protected String getHeaders() {
        return "HTTP/1.1 " +
                getStatusCode().getStatusCodeNumber() +
                SPACE +
                getStatusCode().getDescription() + LINE_END +
                addHeaders() + //Add Additional Headers.
                "Content-Length: " + getContentLength() + LINE_END +
                "Content-Type: " + getContentType();
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void overWriteBody(String s) {
        body = new StringBuilder(s);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getResponse() {
        return toHttpResponse();
    }

    public abstract String toHttpResponse();

    public abstract int getContentLength();
}
