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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.plugin.dom.exception.InvalidStateException;

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
    private Model model;
    private OutputStream out;
    private InputStream in;
    private BufferedReader reader = null;

    public Client(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        model = controller.getModel();
    }

    @Override
    public void run() {
        LOG.debug("Connecting on [" + Thread.currentThread().getName() + "]");

        HttpResponseHandler responseHandler = null;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            HttpRequest request = readInputStream(in);
            handleSocketHandlers(request, clientSocket);
            responseHandler = handleRequestHandlers(request);

            //only remove if its http 1.0
            controller.removeConnection(this);
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

            controller.removeConnection(this);

            try {
                if (responseHandler != null) {
                    //is this needed?
                    out.write(responseHandler.getResponse().getBytes("UTF-8"));
                }

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
        State state = request.getState();

        if (state.isErrorState()) {
            StatusCode statusCode = request.getStatusCode();
            if (statusCode != null) {
                response.setStatusCode(statusCode);
            } else {
                response.setStatusCode(BAD_REQUEST);
            }
        } else {
            RequestHandler handler = getHandlerByRequestMethod(request, response);
            if (handler != null) {
                handler.handle(request, response);
            } else {
                response.setStatusCode(NOT_FOUND);
            }
        }

        response.buildResponse();
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
    private RequestHandler getHandlerByRequestMethod(HttpRequest request, HttpResponseHandler response) {
        RequestHandler handler = null;
        if (request.getPath() != null) {

            if (controller.getSocketMap() != null &&
                    controller.getSocketMap().containsKey(request.getPath()) &&
                    request.getStatusCode() == SWITCHING_PROTOCOL) {
                handler = controller.getSocketMap().get(request.getPath());
                response.setWebsocketAcceptString(request.getUpgradeSecureKeyAnswer());
            }

            if (request.getRequestMethod().equals(RequestType.GET) &&
                    controller.getGetMap() != null &&
                    controller.getGetMap().containsKey(request.getPath())) {
                handler = controller.getGetMap().get(request.getPath());
                response.setStatusCode(ACCEPTED);
            }

            if (request.getRequestMethod().equals(RequestType.POST) &&
                    controller.getPostMap() != null &&
                    controller.getPostMap().containsKey(request.getPath())) {
                handler = controller.getPostMap().get(request.getPath());
                response.setStatusCode(ACCEPTED);
            }

        } else {
            throw new InvalidStateException("Path is not found and made it in this section, bad state.");
        }
        return handler;
    }
}
