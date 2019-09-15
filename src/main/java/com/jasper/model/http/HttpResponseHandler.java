package com.jasper.model.http;

import com.jasper.model.http.enums.StatusCode;

import java.util.HashMap;
import java.util.Map;

import static com.jasper.model.http.enums.StatusCode.NOT_FOUND;

/**
 * All variables and functions shared for the HTTP protocol
 */
public abstract class HttpResponseHandler {

    public final static String LINE_END = "\r\n";
    public final static String DOUBLE_LINE_END = "\r\n\r\n";
    public final static String SPACE = " ";

    private StatusCode statusCode = NOT_FOUND; //status code of request.
    private StringBuilder body = new StringBuilder();
    private String httpVersion;
    private String webSocketAccept;
    private Map<String, String> headers = new HashMap<>();

    public String getWebsocketAcceptString() {
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

    public StringBuilder getHeaders() {
        StringBuilder response = new StringBuilder();
        for (String key : headers.keySet()) {
            response.append(key)
                    .append(":")
                    .append(headers.get(key));
        }
        return response;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getResponse() {
        return toHttpResponse();
    }

    public abstract String toHttpResponse();
}
