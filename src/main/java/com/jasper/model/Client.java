package com.jasper.model;

import com.jasper.controller.Controller;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.RequestHandler;
import com.jasper.model.request.RequestParser;
import com.jasper.model.response.HttpResponse;
import com.jasper.model.response.HttpResponseHandler;
import com.jasper.model.response.SocketSwitchingResponse;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import static com.jasper.model.httpenums.StatusCode.ACCEPTED;
import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;
import static com.jasper.model.httpenums.StatusCode.NOT_FOUND;
import static com.jasper.model.httpenums.StatusCode.SWITCHING_PROTOCOL;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 */
public class Client implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(Client.class);

    private Socket clientSocket;
    private Controller controller;
    private OutputStream out;
    private InputStream in;
    private BufferedReader reader = null;

    public Client(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
    }

    @Override
    public void run() {
        LOG.debug("Connecting on [" + Thread.currentThread().getName() + "]");

        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            HttpRequest request = readSendRequest(in, out, clientSocket);
            String connection = request.getHeaders().getOrDefault("Connection", "close");

            if (request.getHeaders().containsKey("Connection") &&
                    request.getHeaders().get("Connection").equals("keep-alive")) {
                while (!connection.equals("close")) {
                    if(request.getHeaders().containsKey("Connection") && request.isUpgradingConnection()){
                        //do WS
                        readSendSocket(in,out, clientSocket);
                    }else {
                        HttpRequest request1 = readSendRequest(in, out, clientSocket);
                        connection = request1.getHeaders().getOrDefault("Connection", "close");
                    }
                }
            }
        } catch (SocketException e) {
            LOG.warn("Disconnected client by a Socket error, probably disconnected by user.");
        } catch (IOException e) {
            LOG.warn("Disconnected client by Input output error");
        } catch (Exception e) {
            LOG.warn("Disconnected client by a general exception.", e);
        } catch (Throwable e) {
            LOG.warn("Disconnected client by a Throwable exception!", e);
        } finally {
            LOG.info("End of request on [" + Thread.currentThread().getName() + "]");

            try {
                controller.removeConnection(this);

                in.close();
                out.flush();
                out.close();
                clientSocket.close();

            } catch (IOException e) {
                LOG.warn("IOEXCEPTION:", e);
                controller.addStringToLog("[ Error ] IOException, socket is closed");
            }
        }
    }

    /**
     * Read Websocket connection.
     * @param in
     * @param out
     * @param clientSocket
     */
    private void readSendSocket(InputStream in, OutputStream out, Socket clientSocket) {
        System.out.println();
    }

    public HttpRequest readSendRequest(InputStream in, OutputStream out, Socket clientSocket) throws IOException {
        HttpResponseHandler responseHandler = null;

        //Initial request.
        HttpRequest request = readInputStream(in);
        handleSocketHandlers(request, clientSocket);
        responseHandler = handleRequestHandlers(request);

        if (responseHandler.getHttpVersion() != null &&
                responseHandler.getHttpVersion().equals("1.1") &&
                request.getHeaders().get("Connection").equals("keep-alive")) {
            responseHandler.addHeader("Connection", "keep-alive");
        }
        out.write(responseHandler.getResponse().getBytes(StandardCharsets.UTF_8));
        return request;
    }

    /**
     * Read and parse characters from the stream, at the same time.
     */
    @Nonnull
    private HttpRequest readInputStream(@Nonnull InputStream inputStream) {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        RequestParser requestParser = new RequestParser();
        HttpRequest request = requestParser.getRequest();
        State state = request.getState();

        while (!state.isErrorState() && !state.isDone()) {
            try {
                request = requestParser.getRequest();
                char c = (char) reader.read();
                requestParser.nextCharacter(c);
                state = request.getState();
            } catch (IOException ex) {
                request.setState(State.ERROR);
                request.setStatusCode(BAD_REQUEST);
                break;//on error escape.
            }
        }

        return request;
    }

    private void handleSocketHandlers(@Nonnull HttpRequest request, @Nonnull Socket clientSocket) throws SocketException {
        switch (request.getStatusCode()) {
            case SWITCHING_PROTOCOL:
                clientSocket.setSoTimeout(0); //timeout to make a clientsocket Idle.
                break;
            default:
                clientSocket.setSoTimeout(5000); //timeout to make a clientsocket Idle.
                clientSocket.setSoLinger(true, 10000); //timeout to close the socket.
                break;
        }
    }

    @Nonnull
    public HttpResponseHandler handleRequestHandlers(@Nonnull HttpRequest request) throws UnsupportedEncodingException {
        HttpResponseHandler response = getHandlerByRequest(request);
        RequestHandler handler = getHandlerByRequestMethod(request);

        StatusCode statusCode = null;
        State state = request.getState();
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

    @Nonnull
    private HttpResponseHandler getHandlerByRequest(@Nonnull HttpRequest request) {
        switch (request.getStatusCode()) {
            case SWITCHING_PROTOCOL:
                return new SocketSwitchingResponse();
            default:
                return new HttpResponse();
        }
    }

    @CheckForNull
    private RequestHandler getHandlerByRequestMethod(HttpRequest request) {
        RequestHandler handler = null;

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
}