package com.jasper.model;

import com.jasper.controller.CommandController;
import com.jasper.controller.Controller;
import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.requestenums.RequestType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 */
public class ClientWorkerRunnable implements Runnable {

    protected Socket clientSocket;
    private Controller controller;
    private OutputStream out;
    private InputStream in;
    private BufferedReader reader = null;
    private CommandController commandController;
    private AtomicBoolean isReceivingInput = new AtomicBoolean(true);
    private boolean isTimedOut;

    ClientWorkerRunnable(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        commandController = controller.getCommandController();
    }

    @Override
    public void run() {
        System.out.println("Connecting on [" + Thread.currentThread().getName() + "]");

        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            HttpRequest request = new HttpRequest();
            while (isReceivingInput()) {

                //Becomes more like read headers before reading and responding to the rest.
                String stringFromInput = readInputStream(in); //Read input from stream.

                if (stringFromInput != null && stringFromInput.equals("")) {
                    controller.addStringToLog(stringFromInput); //add to screen
                } else {
                    commandController.procesCommand(this, stringFromInput, request);
                    isReceivingInput.set(false);
                }
            }

            //send request.
            out.write(request.toString().getBytes("UTF-8"));
            controller.addStringToOutputLog(request.toString());
            controller.addStringToLog("Successfully  disconnected client.");
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

    private boolean isTimedOut() {
        return isTimedOut;
    }

    private boolean isReceivingInput() {
        return isReceivingInput.get();
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        int c;
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        StringBuilder response = new StringBuilder();
        RequestType requestType = null;

        while ((c = reader.read()) != -1) {
            response.append(Character.toChars(c));
            System.out.println(response);

            //When request length is 7 check if valid request, keep going if it is.
            if (response.length() < 7) {
                //check request is valid.

                String requestTypeIncoming = response.toString();
                //Go through all requestTypes.
                for (RequestType request : RequestType.values()) {

                    //start with these names
                    String[] inputString = requestTypeIncoming.split(" ");
                    if (request.name().startsWith(requestTypeIncoming.toUpperCase()) || inputString.length == 2) {
                        if (inputString[0] != null && inputString.length == 2) {
                            requestType = RequestType.valueOf(inputString[0]);
                            break;
                        }

                    } else {
                        //no valid input found.
                        System.out.println("Invalid request found");
                        break;
                    }
                }
            }

            if (requestType != null || response.length() > 8) {
                break;
            }
        }

        //Check next part the content length when length is wrong return 400 statuscode.
        return response.toString();
    }

}
