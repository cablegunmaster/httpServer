package com.jasper.model.response;

import com.jasper.model.httpenums.StatusCode;
import java.io.UnsupportedEncodingException;

/**
 * Response for the Socket switching when the upgrade is requested Going from HTTP to websocket protocol,
 * bidirectioneel binair protcol.
 */
public class SocketSwitchingResponse extends HttpResponseHandler {

    String websocketAccept;

    //TODO if response need modiciations?
    public String getWebsocketAcceptString() {
        return websocketAccept;
    }

    public void setWebsocketAcceptString(String websocketAccept) {
        this.websocketAccept = websocketAccept;
    }

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

        //first version keep it simple:
        response.append("Upgrade: WebSocket").append(LINE_END)
                .append("Connection: Upgrade").append(LINE_END)
                .append("Sec-WebSocket-Protocol: chat").append(LINE_END)
                .append("Sec-WebSocket-Accept:").append(getWebsocketAcceptString()).append(LINE_END)
                .append(DOUBLE_LINE_END)
                .append(getBody())
                .append(DOUBLE_LINE_END);

        return response.toString();
    }
}
