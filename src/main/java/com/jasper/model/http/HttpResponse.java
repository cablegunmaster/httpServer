package com.jasper.model.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

/**
 * Http response object for sending back a HTTP response for GET or POST.
 */
public class HttpResponse extends HttpResponseBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(HttpResponseBuilder.class);

    /**
     * Output of the whole file, the full request as a String to be send back to the client. TODO improve this toString function to be able
     * to send back an extra support.
     */
    @Nonnull
    @Override
    public String toHttpResponse() {
        LOG.debug("Create http response length: {} ", this.getContentLength());
        return getHeaders() +
                DOUBLE_LINE_END +
                getBody() +
                DOUBLE_LINE_END;
    }

    //get length of bytes.
    public int getContentLength() {
        return getBody().getBytes(StandardCharsets.UTF_8).length;
    }
}
