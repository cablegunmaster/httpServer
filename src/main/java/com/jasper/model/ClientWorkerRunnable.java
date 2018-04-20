package com.jasper.model;

import com.jasper.controller.Controller;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.RequestHandler;
import com.jasper.model.request.RequestParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 */
public class ClientWorkerRunnable implements Runnable {

    protected Socket clientSocket;
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
        HttpResponse httpResponse = new HttpResponse();

        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            handleHandlers(readInputStream(in), httpResponse);

            controller.removeConnection(this);
        } catch (SocketException e) {
            System.err.println("Disconnected client by a Socket error, probably disconnected by user.");
        } catch (IOException e) {
            //report somewhere
            System.err.println("Disconnected client by Input output error");
        } catch (Exception e) {
            System.err.println("Disconnected client by a general exception.");
            System.err.println(e);
            e.printStackTrace();
        } catch (Throwable e) {
            System.err.println("Disconnected client by a Throwable exception!");
            System.err.println(e);
            e.printStackTrace();
        } finally {
            System.out.println("End of request on [" + Thread.currentThread().getName() + "]");

            try {
                out.write(httpResponse.getResponse().getBytes("UTF-8"));
                in.close();
                out.flush();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                controller.addStringToLog("Error closing the socket");
            }
        }
    }

    /**
     * Read and parse characters from the stream, at the same time.
     *
     * @param inputStream
     * @throws IOException
     */
    private HttpRequest readInputStream(InputStream inputStream) throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        RequestParser requestParser = new RequestParser();
        State state = requestParser.getRequest().getState();

        while(!state.isErrorState() &&
              !state.isDone()){
            char c = (char) reader.read();
            requestParser.nextCharacter(c);
            state = requestParser.getRequest().getState();
        }

        return requestParser.getRequest();
    }

    public void handleHandlers(HttpRequest request, HttpResponse response) throws UnsupportedEncodingException {

        if (!request.getState().isErrorState()) {

            if (request.getRequestMethod().equals(RequestType.GET)) {
                if (request.getPath() != null) {
                    if (controller.getModel().getGetMap().containsKey(request.getPath())) {
                        RequestHandler handler = (RequestHandler) controller.getModel().getGetMap().get(request.getPath());
                        handler.handle(request, response);
                        response.setStatusCode(StatusCode.ACCEPTED);
                    }
                }

            } else{
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

            if(request.getStatusCode() != null){
                response.setStatusCode(request.getStatusCode());
            }else{
                response.setStatusCode(StatusCode.BAD_REQUEST);
            }
        }

        response.buildResponse();
    }
}
