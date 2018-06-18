package com.jasper.model.response;

import com.jasper.model.httpenums.StatusCode;
import java.io.UnsupportedEncodingException;

public class SocketResponse extends HttpResponseHandler {

    /**
     * Output of the socket response. To send back an socket response??
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

        response.append("HTTP/1.1")
                .append(SPACE)
                .append(StatusCode.SWITCHING_PROTOCOL.getStatusCodeNumber())
                .append(SPACE)
                .append(StatusCode.SWITCHING_PROTOCOL.getDescription())
                .append(LINE_END);

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
