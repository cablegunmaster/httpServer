package com.jasper.model;

import com.jasper.controller.CommandController;
import com.jasper.controller.Controller;
import com.jasper.model.request.HttpRequest;

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
    private CommandController commandController;
    private boolean isReceivingInput;
    private boolean isTimedOut;

    ClientWorkerRunnable(Socket clientSocket, Controller controller) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        commandController = controller.getCommandController();
    }

    @Override
    public void run() {

        try {
            isReceivingInput = true;
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            HttpRequest request = new HttpRequest();
            //TODO missing timeout.
            while (isReceivingInput()) {
                String stringFromInput = readInputStream(in); //Read input from stream.
                if (stringFromInput != null && stringFromInput.equals("")) {
                    controller.addStringToLog(stringFromInput); //add to screen

                } else {
                    commandController.procesCommand(this, stringFromInput, request);
                    isReceivingInput = false;
                }
            }

            //send request.
            out.write(request.toString().getBytes("UTF-8"));
            controller.addStringToOutputLog(request.toString());
            controller.addStringToLog("Succesfully disconnected client.");
            controller.getModel().removeConnection(this);

            try {
                if (clientSocket.isConnected()) {
                    out.flush();
                    out.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                controller.addStringToLog("Error closing the socket");
            } finally {
                clientSocket.close();
                controller.addStringToLog("Error closing the socket");
            }

        } catch (SocketException e) {
            System.err.println("Disconnected client by a Socket error, probably disconnected by user.");
            System.err.println(e);
            e.printStackTrace();
        } catch (IOException e) {
            //report somewhere
            System.err.println("Disconnected client by Input output error");
            System.err.println(e);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Disconnected client by a general exception.");
            System.err.println(e);
            e.printStackTrace();
        } catch (Throwable e) {
            System.err.println("Disconnected client by a Throwable exception!");
            System.err.println(e);
            e.printStackTrace();
        }
    }

    private boolean isTimedOut() {
        return isTimedOut;
    }

    private boolean isReceivingInput() {
        return isReceivingInput;
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        String line;
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        while ((line = reader.readLine()) != null || !isTimedOut()) {
            if (line != null && !line.equals("")) {
                return line;
            }
        }
        return "";
    }

}
