package com.jasper.model.request;

import com.jasper.model.request.requestenums.RequestType;
import com.jasper.model.request.requestenums.StatusCode;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Model for a request to be send / used.
 */
public class HttpRequest {

    private RequestType requestMethod = null;
    private String requestpath = ""; //request path "/index.html"
    private String localPath = ""; //Local directory, should be set from a properties file.
    private StatusCode statusCode = null;

    private File content = null; //file reference, for content the <HTML> etc of the request.

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

    /**
     * Get the full request as a String.
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();


        byte[] s = null;

        //TODO missing sending request numbers like 200, 404, 500 etc..
        String content =
                "<html>" +
                        "<body>" +
                        "<h1>This in a succesfull request</h1>" +
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

        buffer.append("Content-Length: " + contentInBytes.length + "\r\n");
        buffer.append("Content-Type: text/html; charset=utf-8");
        buffer.append("\r\n\r\n");
        buffer.append(content);
        buffer.append("\r\n\r\n");

        return buffer.toString();
    }
}
