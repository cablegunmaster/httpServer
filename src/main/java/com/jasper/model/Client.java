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

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;

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
    private HttpRequest readInputStream(InputStream inputStream) {

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

    private void handleSocketHandlers(HttpRequest request, Socket clientSocket) throws SocketException {
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

    public HttpResponseHandler handleRequestHandlers(HttpRequest request) throws UnsupportedEncodingException {

        HttpResponseHandler response;

        switch (request.getStatusCode()) {
            case SWITCHING_PROTOCOL:
                response = new SocketSwitchingResponse();
                break;
            default:
                response = new HttpResponse();
                break;
        }

        State state = request.getState();
        if (state.isErrorState()) {
            StatusCode statusCode = request.getStatusCode();
            if (statusCode != null) {
                response.setStatusCode(statusCode);
            } else {
                response.setStatusCode(BAD_REQUEST);
            }
        }

        if (!state.isErrorState()) {
            if (request.getRequestMethod().equals(RequestType.GET)) {
                if (request.getPath() != null) {
                    if (model.getGetMap().containsKey(request.getPath())) {
                        RequestHandler handler = (RequestHandler) controller.getModel().getGetMap().get(request.getPath());
                        handler.handle(request, response);
                        response.setStatusCode(StatusCode.ACCEPTED);
                    }
                }
            } else {
                response.setStatusCode(StatusCode.NOT_FOUND);
            }

            if (request.getRequestMethod().equals(RequestType.POST)) {
                if (request.getPath() != null) {
                    if (model.getPostMap().containsKey(request.getPath())) {
                        RequestHandler handler = (RequestHandler) controller.getModel().getPostMap().get(request.getPath());
                        handler.handle(request, response);
                        response.setStatusCode(StatusCode.ACCEPTED);
                    }
                }
            } else {
                response.setStatusCode(StatusCode.NOT_FOUND);
            }
        }

        response.buildResponse();
        return response;
    }
}
