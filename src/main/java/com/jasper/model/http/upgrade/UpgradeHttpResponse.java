package com.jasper.model.http.upgrade;

import com.jasper.model.http.HttpResponseBuilder;
import com.jasper.model.http.enums.StatusCode;

import javax.annotation.Nonnull;

/**
 * Response for the Socket switching when the upgrade is requested Going from HTTP to websocket protocol,
 * bidirectioneel binair protcol.
 */
public class UpgradeHttpResponse extends HttpResponseBuilder {

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
                "Sec-WebSocket-Protocol: chat" + LINE_END +
                "Upgrade: websocket" + LINE_END +
                "Connection: Upgrade" + LINE_END +
                "Sec-WebSocket-Accept: " + getWebsocketAcceptString() +
                LINE_END +
                LINE_END;
    }


    @Override
    public int getContentLength() {
        return 0;
    }
}
