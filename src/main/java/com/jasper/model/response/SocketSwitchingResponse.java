package com.jasper.model.response;

import com.jasper.model.httpenums.StatusCode;

import javax.annotation.Nonnull;

/**
 * Response for the Socket switching when the upgrade is requested Going from HTTP to websocket protocol,
 * bidirectioneel binair protcol.
 */
public class SocketSwitchingResponse extends HttpResponseHandler {

    /**
     * Output of the socket response. To send back an socket response??
     */
    @Nonnull
    @Override
    public String toHttpResponse() {
        return "HTTP/1.1" +
                SPACE +
                StatusCode.SWITCHING_PROTOCOL.getStatusCodeNumber() +
                SPACE +
                StatusCode.SWITCHING_PROTOCOL.getDescription() +
                LINE_END +
                getHeaders() + LINE_END +
                "Upgrade: websocket" + LINE_END +
                "Connection: Upgrade" + LINE_END +
                "Sec-WebSocket-Accept: " + getWebsocketAcceptString() +
                LINE_END +
                LINE_END;
    }
}
