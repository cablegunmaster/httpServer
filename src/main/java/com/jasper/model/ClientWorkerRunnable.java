package com.jasper.model;

import com.jasper.controller.Controller;
import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.RequestParser;
import com.jasper.model.request.requestenums.StatusCode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            HttpRequest request = new HttpRequest();
            request = readInputStream(in, request); //Read input from stream.

            out.write(request.toString().getBytes("UTF-8"));  //send request.
            controller.addStringToOutputLog(request.toString());

            if(request.getStatusCode() == StatusCode.OK) {
                controller.addStringToLog("[Succes] parsed client");
            } else {
                controller.addStringToLog("[Error]  disconnected client.");
            }

            controller.getModel().removeConnection(this);

        } catch (SocketException e) {
            System.err.println("Disconnected client by a Socket error, probably disconnected by user.");
            //System.err.println(e);
            //e.printStackTrace();
        } catch (IOException e) {
            //report somewhere
            System.err.println("Disconnected client by Input output error");
            //System.err.println(e);
            //e.printStackTrace();
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
     * @param inputStream
     * @param request
     * @return whole request?
     * @throws IOException
     */
    private HttpRequest readInputStream(InputStream inputStream, HttpRequest request) throws IOException {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        StringBuilder response = new StringBuilder();
        Boolean isReadingRequest = true;

        RequestParser requestParser = new RequestParser();

        while (isReadingRequest && reader.ready()) {

            char c = (char) reader.read();
            requestParser.nextCharacter(c);
        }

        if (response.toString().equals("")) {
            controller.addStringToLog(response.toString()); //show request on screen.
        }

        return request;
    }

}
