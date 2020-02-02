package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.HttpRequest;
import com.jasper.model.IRequestHandler;
import com.jasper.model.http.HttpResponse;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.upgrade.UpgradeHttpResponse;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

import static com.jasper.model.http.enums.StatusCode.*;

public class RequestHandler {

    private Controller controller;

    public RequestHandler(Controller controller) {
        this.controller = controller;
    }

    @Nonnull
    public HttpResponseHandler handleRequest(@Nonnull HttpRequest request) throws UnsupportedEncodingException {
        HttpResponseHandler response = getHandlerByRequest(request);
        IRequestHandler handler = getHandlerByRequestMethod(request);

        HttpState state = request.getState();
        if (handler != null) {
            handler.handle(request, response);
            response.setStatusCode(ACCEPTED);

            if (request.getUpgradeSecureKeyAnswer() != null) {
                response.setWebsocketAcceptString(request.getUpgradeSecureKeyAnswer());

                if (request.getHeaders().containsKey("Sec-WebSocket-Protocol")) {
                    response.addHeader("Sec-WebSocket-Protocol", request.getHeaders().get("Sec-WebSocket-Protocol"));
                }
            }

        } else if (state.isErrorState()) {
            response.setStatusCode(request.getStatusCode());
        } else {
            response.setStatusCode(NOT_FOUND);
        }

        response.setHttpVersion(request.getHttpVersion());
        return response;
    }

    @CheckForNull
    private IRequestHandler getHandlerByRequestMethod(HttpRequest request) {
        IRequestHandler handler = null;

        if (request.getPath() != null) {
            String path = request.getPath();

            switch (request.getRequestMethod()) {
                case GET:
                    if (request.getStatusCode() == SWITCHING_PROTOCOL &&
                            controller.getSocketMap().containsKey(path)) {
                        handler = controller.getSocketMap().get(path);
                    } else if (controller.getGetMap().containsKey(path)) {
                        handler = controller.getGetMap().get(path);
                    }
                    break;
                case POST:
                    if (controller.getPostMap().containsKey(path)) {
                        handler = controller.getPostMap().get(path);
                    }
                    break;
                default:
                    return null;
            }
        }
        return handler;
    }


    void handleSocketHandlers(@Nonnull HttpRequest request, @Nonnull Socket clientSocket) throws SocketException {
        if (request.getStatusCode() == SWITCHING_PROTOCOL) {
            clientSocket.setSoTimeout(0); //timeout to make a clientsocket Idle.
        } else {
            clientSocket.setSoTimeout(500000); //timeout to make a clientsocket Idle.
            clientSocket.setSoLinger(true, 100000); //timeout to close the socket.
        }
    }

    @Nonnull
    private HttpResponseHandler getHandlerByRequest(@Nonnull HttpRequest request) {
        if (request.getStatusCode() == SWITCHING_PROTOCOL) {
            return new UpgradeHttpResponse();
        }
        return new HttpResponse();
    }
}
