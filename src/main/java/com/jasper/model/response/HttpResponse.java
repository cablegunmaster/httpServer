package com.jasper.model.response;

import java.io.UnsupportedEncodingException;
import java.util.Map;

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
        return "HTTP/1.1 " +
                getStatusCode().getStatusCodeNumber() +
                SPACE +
                getStatusCode().getDescription() + LINE_END +
                getHeaderBuild() + //Add Additional Headers.
                "Content-Length: " + getContentLength() + LINE_END +
                "Content-Type: text/html; charset=utf-8" +
                DOUBLE_LINE_END +
                getBody() +
                DOUBLE_LINE_END;
    }

    private StringBuilder getHeaderBuild() {
        Map<String, String> headers = getHeaders();

        StringBuilder response = new StringBuilder();
        for (String key : headers.keySet()) {
            response.append(key)
                    .append(":")
                    .append(headers.get(key));
        }
        return response;
    }

    //get length of bytes.
    private Integer getContentLength() {
        byte[] contentInBytes = null;

        try {
            contentInBytes = getBody().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return contentInBytes != null ? contentInBytes.length : 0;
    }
}
