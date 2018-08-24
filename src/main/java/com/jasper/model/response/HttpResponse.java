package com.jasper.model.response;

import java.io.UnsupportedEncodingException;

/**
 * Http response object for sending back a HTTP response for GET or POST.
 */
public class HttpResponse extends HttpResponseHandler {

    /**
     * Output of the whole file, the full request as a String to be send back to the client. TODO improve this toString function to be able
     * to send back an extra support.
     */
    @Override
    public String toHttpResponse() {

        StringBuilder response = new StringBuilder();
        byte[] contentInBytes = null;

        try {
            contentInBytes = getBody().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        response.append("HTTP/1.1 ")
                .append(getStatusCode().getStatusCodeNumber())
                .append(SPACE)
                .append(getStatusCode().getDescription()).append(LINE_END);

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
