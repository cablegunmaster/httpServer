package com.jasper.model;

import com.jasper.controller.CommandController;
import com.jasper.controller.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Jasper Lankhorst on 20-11-2016.
 */
public class ClientWorkerRunnable implements Runnable {

    protected Socket clientSocket;
    private Controller controller;
    private OutputStream os;
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
            InputStream inputStream = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            //printWriter = new PrintWriter(os, true);

            while (isReceivingInput()) {
                String stringFromInput = readInputStream(inputStream); //Read input from stream.
                if (stringFromInput != null && stringFromInput.equals("")) {
                    controller.addStringToLog(stringFromInput); //add to screen
                }else{
                    isReceivingInput = false;
                }
            }

            byte[]s = null;
            byte[]contentInBytes = null;

            String content="\r\n\r\n" +
                    "<html>" +
                        "<body>" +
                            "<h1>This is á succesfull request</h1>" +
                        "</body>" +
                    "</html>" +
              "\r\n\r\n";

            String t="HTTP/1.1 200 OK\r\n";
            s=t.getBytes("UTF-8");
            os.write(s);

            contentInBytes = content.getBytes("UTF-8");
            t="Content-Length: "+contentInBytes.length+"\r\n";
            s=t.getBytes("UTF-8");
            os.write(s);
            t="Content-Type: text/html; charset=utf-8";
            s=t.getBytes("UTF-8");
            os.write(s);
            os.write(contentInBytes);

            controller.addStringToLog("Succesfully disconnected client.");
            controller.getModel().removeConnection(this);

            try {
                if(clientSocket.isConnected()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (SocketException e) {
            System.err.println("Disconnected client by a Socket error, probably disconnected by user.");
        } catch (IOException e) {
            //report somewhere
            e.printStackTrace();
            System.err.println("Disconnected client by Input output error");
        } catch (Exception e) {
            e.getCause();
            System.err.println("Disconnected client by a general exception.");
            e.getStackTrace();
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
