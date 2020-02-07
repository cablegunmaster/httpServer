package com.jasper.model;

import java.net.Socket;

/**
 * Class for request
 */
public abstract class Request {

    private Socket socket;
    private boolean keepConnected;

    Request(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isKeepConnected() {
        return keepConnected;
    }

    public void setKeepConnected(boolean keepConnected) {
        this.keepConnected = keepConnected;
    }
}
