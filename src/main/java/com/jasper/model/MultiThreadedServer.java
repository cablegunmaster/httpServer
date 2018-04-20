package com.jasper.model;

import com.jasper.controller.Controller;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Jasper Lankhorst on 19-11-2016.
 */
public class MultiThreadedServer implements Runnable {

    private int serverPort = 80;
    private ServerSocket serverSocket = null;
    private static boolean isStopped = false;
    private Controller controller = null;

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
            if(!isStopped()) {
                sendRequest(clientSocket);
            }
        }
        controller.addStringToLog("[ OK ] Server Thread exiting....");
    }

    private Socket awaitIncomingConnection() {
        Socket clientSocket = null;
        try {
            controller.addStringToLog("[ OK ] Server is awaiting connections...");

            this.serverSocket.setReuseAddress(true);
            clientSocket = this.serverSocket.accept();

            clientSocket.setSoTimeout(2); //timeout to make a clientsocket Idle.
            clientSocket.setSoLinger(true, 10000); //timeout to close the socket.

        } catch (SocketException e) {
            isStopped = true;
            clientSocket = null;
        } catch (IOException e) {
            isStopped = true;
            throw new RuntimeException("[ Error ] accepting client connection", e);
        }
        return clientSocket;
    }

    private void sendRequest(Socket clientSocket) {
        if (clientSocket != null && controller != null) {
            controller.addStringToLog("Connection made..");

            ClientWorkerRunnable clientWorkerRunnable = new ClientWorkerRunnable(clientSocket, controller);
            controller.addConnection(clientWorkerRunnable);
            Thread t = new Thread(clientWorkerRunnable);
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
