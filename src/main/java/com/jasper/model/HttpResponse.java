package com.jasper.model;

import com.jasper.model.httpenums.StatusCode;
import java.io.UnsupportedEncodingException;

public class HttpResponse {

    private final static String LINE_END = "\r\n";
    private final static String DOUBLE_LINE_END = "\r\n\r\n";

    private String response = null;
    private StringBuilder body = new StringBuilder();
    private StatusCode statusCode = null; //status code of request.
    private double httpVersion = 1.1;

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void write(String s) {
        body.append(s);
    }

    private String getBody() {
        return body.toString();
    }

    public double getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(double httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void buildResponse() {
        response = toString();
    }

    public String getResponse() {
        return response;
    }

    /**
     * Output of the whole file, the full request as a String to be send back to the client.
     * TODO improve this toString function to be able to send back an extra support.
     */
    @Override
    public String toString() {

        StringBuilder response = new StringBuilder();
        byte[] contentInBytes = null;

        try {
            contentInBytes = getBody().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        response.append("HTTP/1.1 ").append(getStatusCode().getStatusCodeNumber()).append(getStatusCode().getDescription()).append(LINE_END);

        Integer contentLength = 0;
        if (contentInBytes != null) {
            contentLength = contentInBytes.length;
        }

        response.append("Content-Length: ").append(contentLength).append(LINE_END);
        response.append("Content-Type: text/html; charset=utf-8");
        response.append(DOUBLE_LINE_END);
        response.append(getBody());
        response.append(DOUBLE_LINE_END);

        return response.toString();
    }
}
