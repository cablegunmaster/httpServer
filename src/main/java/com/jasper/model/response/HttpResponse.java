package com.jasper.model.response;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Http response object for sending back a HTTP response for GET or POST.
 */
public class HttpResponse extends HttpResponseHandler {

    /**
     * Output of the whole file, the full request as a String to be send back to the client. TODO improve this toString function to be able
     * to send back an extra support.
     */
    @Nonnull
    @Override
    public String toHttpResponse() {
        return "HTTP/1.1 " +
                getStatusCode().getStatusCodeNumber() +
                SPACE +
                getStatusCode().getDescription() + LINE_END +
                getHeaders() +  LINE_END + //Add Additional Headers.
                "Content-Length: " + getContentLength() + LINE_END +
                "Content-Type: text/html; charset=utf-8" +
                DOUBLE_LINE_END +
                getBody() +
                DOUBLE_LINE_END;
    }


    //get length of bytes.
    @Nonnull
    private Integer getContentLength() {
        return getBody().getBytes(StandardCharsets.UTF_8).length;
    }
}
