package com.jasper.model.response;

import com.jasper.model.httpenums.StatusCode;

import java.util.Map;

/**
 * Response for the Socket switching when the upgrade is requested Going from HTTP to websocket protocol,
 * bidirectioneel binair protcol.
 */
public class SocketSwitchingResponse extends HttpResponseHandler {

    /**
     * Output of the socket response. To send back an socket response??
     */
    @Override
    public String toHttpResponse() {

        String response = "HTTP/1.1" +
                SPACE +
                StatusCode.SWITCHING_PROTOCOL.getStatusCodeNumber() +
                SPACE +
                StatusCode.SWITCHING_PROTOCOL.getDescription() +
                LINE_END +
                getHeaderBuild() +
                "Upgrade: websocket" + LINE_END +
                "Connection: Upgrade" + LINE_END +
                "Sec-WebSocket-Accept: " + getWebsocketAcceptString() +
                LINE_END +
                LINE_END;
        return response;
    }

    private StringBuilder getHeaderBuild() {
        Map<String, String> headers = getHeaders();

        StringBuilder response = new StringBuilder();
        for (String key : headers.keySet()) {
            response.append(key);
            response.append(": ");
            response.append(headers.get(key));
            response.append(LINE_END);
        }

        return response;
    }

}
