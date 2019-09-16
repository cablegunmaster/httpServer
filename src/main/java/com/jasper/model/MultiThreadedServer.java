package com.jasper.model;

import com.jasper.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Jasper Lankhorst on 19-11-2016.
 */
public class MultiThreadedServer implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(MultiThreadedServer.class);

    private int serverPort;
    private ServerSocket serverSocket = null;
    private static boolean isStopped = false;
    private Controller controller;

    public MultiThreadedServer(int port, Controller controller) {
        this.serverPort = port;
        this.controller = controller;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = awaitIncomingConnection();
            if (!isStopped() && clientSocket != null) {
                sendRequest(clientSocket);
            }
        }
        controller.addStringToLog("[ OK ] Server Thread exiting....");
    }

    @CheckForNull
    private Socket awaitIncomingConnection() {
        Socket clientSocket = null;
        try {
            controller.addStringToLog("[ OK ] Server is awaiting connections...");
            this.serverSocket.setReuseAddress(true);
            clientSocket = this.serverSocket.accept();
        } catch (SocketException e) {
            isStopped = true;
            LOG.warn("[ Error ] Socket exception happened", e);
        } catch (IOException e) {
            isStopped = true;
            LOG.warn("[ Error ] accepting client connection", e);
        }
        return clientSocket;
    }

    private void sendRequest(Socket clientSocket) {
        if (clientSocket != null && controller != null) {
            controller.addStringToLog("Connection made..");

            Client client = new Client(clientSocket, controller);
            controller.addConnection(client);
            Thread t = new Thread(client);
            t.start();
        }
    }

    private void openServerSocket() {
        if (serverSocket == null || serverSocket.isClosed()) {
            isStopped = false;
            try {
                serverSocket = new ServerSocket(this.serverPort);
            } catch (IOException e) {
                isStopped = true;
            }
        }
    }

    private synchronized boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }
}
