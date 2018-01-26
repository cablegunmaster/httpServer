package com.jasper.model;

import com.jasper.model.requestenums.RequestType;

/**
 * Model for a request to be send / used.
 */
public class HttpRequest {

    //standard a GET otherwise a post.
    private RequestType requestMethod = RequestType.GET;
    private String requestpath = ""; //request path "/index.html"
    private String localPath = ""; //Local directory, should be set from a properties file.

    HttpRequest() {
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

    /**
     * Get the full request as a String.
     */
    @Override
    public String toString() {
        return requestMethod.toString();
    }
}
