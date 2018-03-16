package com.jasper.model.request;

import com.jasper.model.request.requestenums.RequestType;
import com.jasper.model.request.requestenums.State;
import com.jasper.model.request.requestenums.StatusCode;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Model for a request to be send / used.
 */
public class HttpRequest {

    private RequestType requestMethod = null;
    private String requestpath = null; //request path "/index.html"
    private String localPath = null; //Local directory, should be set from a properties file.
    private StatusCode statusCode = null;
    private Long httpVersion;

    private State state = State.READ_METHOD;
    private StringBuilder method = new StringBuilder();
    private Map<String, String> headers = new HashMap<>();

    private StringBuilder headerName = new StringBuilder();
    private StringBuilder headerValue = new StringBuilder();

    public HttpRequest() {
    }

    public void setRequestMethod(RequestType requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RequestType getRequestMethod() {
        return requestMethod;
    }

    public String getRequestpath() {
        return requestpath;
    }

    public void setRequestpath(String requestpath) {
        this.requestpath = requestpath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public State getState() {
        return state;
    }

    public void setState(State status) {
        this.state = status;
    }

    public StringBuilder getMethod() {
        return method;
    }

    public void setMethod(StringBuilder method) {
        this.method = method;
    }

    public void setHttpVersion(long httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public StringBuilder getHeaderName() {
        return headerName;
    }

    public void setHeaderName(StringBuilder headerName) {
        this.headerName = headerName;
    }

    public StringBuilder getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(StringBuilder headerValue) {
        this.headerValue = headerValue;
    }

    /**
     * Output of the whole file, the full request as a String to be send back to the client.
     * TODO improve this toString function to be able to send back an extra support.
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        byte[] s = null;

        //TODO missing sending request numbers like 200, 404, 500 etc..
        String content =
                "<html>" +
                        "<body>" +
                        "<h1>This is á successful request</h1>" +
                        "</body>" +
                        "</html>";

        buffer.append("HTTP/1.1 200 OK\r\n");

        //read the amount of bytes;
        byte[] contentInBytes = null;

        try {
            contentInBytes = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Integer contentLength = 0;
        if (contentInBytes != null) {
            contentLength = contentInBytes.length;
        }

        buffer.append("Content-Length: ").append(contentLength).append("\r\n");
        buffer.append("Content-Type: text/html; charset=utf-8");
        buffer.append("\r\n\r\n");
        buffer.append(content);
        buffer.append("\r\n\r\n");

        return buffer.toString();
    }
}
