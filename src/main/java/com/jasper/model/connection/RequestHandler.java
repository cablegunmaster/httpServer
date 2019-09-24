package com.jasper.model.connection;

import com.jasper.controller.Controller;
import com.jasper.model.HttpRequest;
import com.jasper.model.IRequestHandler;
import com.jasper.model.http.HttpResponse;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.enums.RequestType;
import com.jasper.model.http.upgrade.UpgradeHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

import static com.jasper.model.http.enums.StatusCode.*;

public class RequestHandler {

    private final static Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

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
                    response.addHeader("Sec-WebSocket-Protocol",
                            request.getHeaders().get("Sec-WebSocket-Protocol"));
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
            if (controller.getSocketMap() != null &&
                    controller.getSocketMap().containsKey(request.getPath()) &&
                    request.getStatusCode() == SWITCHING_PROTOCOL) {
                handler = controller.getSocketMap().get(request.getPath());
            }

            if (request.getRequestMethod().equals(RequestType.GET) &&
                    controller.getGetMap() != null &&
                    controller.getGetMap().containsKey(request.getPath())) {
                handler = controller.getGetMap().get(request.getPath());
            }

            if (request.getRequestMethod().equals(RequestType.POST) &&
                    controller.getPostMap() != null &&
                    controller.getPostMap().containsKey(request.getPath())) {
                handler = controller.getPostMap().get(request.getPath());
            }
        }
        return handler;
    }


    public void handleSocketHandlers(@Nonnull HttpRequest request, @Nonnull Socket clientSocket) throws SocketException {
        if (request.getStatusCode() == SWITCHING_PROTOCOL) {
            clientSocket.setSoTimeout(0); //timeout to make a clientsocket Idle.
        } else {
            clientSocket.setSoTimeout(500000); //timeout to make a clientsocket Idle.
            clientSocket.setSoLinger(true, 100000); //timeout to close the socket.
        }
    }

    @Nonnull
    public HttpResponseHandler getHandlerByRequest(@Nonnull HttpRequest request) {
        if (request.getStatusCode() == SWITCHING_PROTOCOL) {
            return new UpgradeHttpResponse();
        }
        return new HttpResponse();
    }
}
