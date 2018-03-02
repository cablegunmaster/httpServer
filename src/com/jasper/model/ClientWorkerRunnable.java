package com.jasper.model;

import com.jasper.controller.Controller;
import com.jasper.model.request.RequestParser;
import com.jasper.model.request.HttpRequest;
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
            //Becomes more like read headers before reading and responding to the rest.
            String stringFromInput = readInputStream(in, request); //Read input from stream.

            if (stringFromInput.equals("")) {
                controller.addStringToLog(stringFromInput); //add to screen
            } else {
                new RequestParser(controller).procesCommand(this, stringFromInput, request);
            }

            //send request.
            out.write(request.toString().getBytes("UTF-8"));
            controller.addStringToOutputLog(request.toString());

            //if not error.
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

    private String readInputStream(InputStream inputStream, HttpRequest request) throws IOException {

        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        StringBuilder response = new StringBuilder();
        Boolean isReadingRequest = true;


        //Do all reading from socket errors here.

        while (isReadingRequest) {

            int c = reader.read();
            response.append(Character.toChars(c));

            //Request a error state when the stream is closed on http1.1?

            //Parameter variable length of request. 7K
            //413 Entity too large
            if(response.length() > 8192){
                return "413";
            }

            if(response.length() > 4 ){
                //get the last part.
                String lastPart = response.substring(response.length() - 4 , response.length());
                if(lastPart.contains("\r\n\r\n")){
                    isReadingRequest = false; // reading completed succesfully
                }
            }
                //URI LENGTH
                //Note: Servers ought to be cautious about depending on URI lengths above 255 bytes, because some older client or proxy
                //implementations might not properly support these lengths.
        }

        return response.toString();
    }

}
