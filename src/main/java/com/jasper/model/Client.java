package com.jasper.model;

import com.jasper.model.socket.models.entity.Frame;

import java.net.Socket;
import java.util.Stack;

public class Client {

    private Socket clientSocket;
    private HttpRequest request;

    private Stack<Frame> frameStack = new Stack<>();
    private StringBuffer messageBuffer = new StringBuffer();
    private boolean keepConnected;

    Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    //empty stringBuffer.
    public void clear(StringBuffer s) {
        s.setLength(0);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public Stack<Frame> getFrameStack() {
        return frameStack;
    }

    public StringBuffer getMessageBuffer() {
        return messageBuffer;
    }

    public boolean isKeepConnected() {
        return keepConnected;
    }

    public void setKeepConnected(boolean keepConnected) {
        this.keepConnected = keepConnected;
    }
}
