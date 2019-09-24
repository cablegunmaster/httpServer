package com.jasper.model;

import com.jasper.controller.Controller;
import com.jasper.model.connection.ConnectionManager;
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
    private static boolean isRunning = true;
    private ServerSocket serverSocket = null;
    private Controller controller;

    public MultiThreadedServer(int port, Controller controller) {
        this.serverPort = port;
        this.controller = controller;
        new Thread(new ConnectionManager(controller)).start();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        openServerSocket();

        while (isRunning()) {
            Socket clientSocket = awaitIncomingConnection();

            if (isRunning() && clientSocket != null) {
                controller.addStringToLog("Connection made..");
                controller.addConnection(new Client(clientSocket));
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
            isRunning = true;
            LOG.warn("[ Error ] Socket exception happened", e);
        } catch (IOException e) {
            isRunning = true;
            LOG.warn("[ Error ] accepting client connection", e);
        }
        return clientSocket;
    }

    private void openServerSocket() {
        if (serverSocket == null || serverSocket.isClosed()) {
            isRunning = true;
            try {
                serverSocket = new ServerSocket(this.serverPort);
            } catch (IOException e) {
                LOG.warn("NOT STARTING WITH CURRENT PORT {}, {}", e, this.serverPort);
                isRunning = false;
            }
        }
    }

    private synchronized boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean stopped) {
        isRunning = stopped;
    }
}
