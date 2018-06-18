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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 */
public class ClientWorkerRunnable implements Runnable {

    private Socket clientSocket;
    private Controller controller;
    private OutputStream out;
    private InputStream in;
    private BufferedReader reader = null;

    ClientWorkerRunnable(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
    }

    @Override
    public void run() {
        System.out.println("Connecting on [" + Thread.currentThread().getName() + "]");

        HttpResponseHandler responseHandler = null;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            responseHandler = handleHandlers(readInputStream(in));

            controller.removeConnection(this);
        } catch (SocketException e) {
            System.err.println("Disconnected client by a Socket error, probably disconnected by user.");
        } catch (IOException e) {
            //report somewhere
            System.err.println("Disconnected client by Input output error");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Disconnected client by a general exception.");
            System.err.println(e);
        } catch (Throwable e) {
            System.err.println("Disconnected client by a Throwable exception!");
            System.err.println(e);
            e.printStackTrace();
        } finally {
            System.out.println("End of request on [" + Thread.currentThread().getName() + "]");

            try {

                if (responseHandler != null) {
                    out.write(responseHandler.getResponse().getBytes("UTF-8"));
                }

                in.close();
                out.flush();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                controller.addStringToLog("[ Error ] IOException, socket is closed");
            }
        }
    }

    /**
     * Read and parse characters from the stream, at the same time.
     *
     * @param inputStream
     * @throws IOException
     */
    private HttpRequest readInputStream(InputStream inputStream) {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        RequestParser requestParser = new RequestParser();
        HttpRequest request = requestParser.getRequest();
        State state = requestParser.getRequest().getState();

        while (!state.isErrorState() &&
                !state.isDone()) {

            try {
                request = requestParser.getRequest();
                char c = (char) reader.read();
                requestParser.nextCharacter(c);
                state = request.getState();
            } catch (IOException ex) {
                request.setState(State.ERROR);
                request.setStatusCode(BAD_REQUEST);
                break;
            }
        }

        return request;
    }

    private HttpResponseHandler handleHandlers(HttpRequest request) throws UnsupportedEncodingException {

        HttpResponseHandler response;
        switch (request.getStatusCode()) {
            case SWITCHING_PROTOCOL:
                response = new SocketSwitchingResponse();
                break;
            default:
                response = new HttpResponse();
                break;
        }

        if (!request.getState().isErrorState()) {

            if (request.getRequestMethod().equals(RequestType.GET)) {
                if (request.getPath() != null) {
                    if (controller.getModel().getGetMap().containsKey(request.getPath())) {
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
                    if (controller.getModel().getPostMap().containsKey(request.getPath())) {
                        RequestHandler handler = (RequestHandler) controller.getModel().getPostMap().get(request.getPath());
                        handler.handle(request, response);
                        response.setStatusCode(StatusCode.ACCEPTED);
                    }
                }
            } else {
                response.setStatusCode(StatusCode.NOT_FOUND);
            }

        } else {

            if (request.getStatusCode() != null) {
                response.setStatusCode(request.getStatusCode());
            } else {
                response.setStatusCode(BAD_REQUEST);
            }
        }

        response.buildResponse();

        return response;
    }
}
