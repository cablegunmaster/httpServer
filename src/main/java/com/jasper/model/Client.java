package com.jasper.model;

import com.jasper.model.socket.models.entity.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.Stack;

public class Client {

    private final static Logger LOG = LoggerFactory.getLogger(Client.class);

    private Socket clientSocket;
    private BufferedReader reader = null;
    private HttpRequest request;

    private Stack<Frame> frameStack = new Stack<>();
    private StringBuffer messageBuffer = new StringBuffer();
    private boolean keepConnected;

    public Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    //empty stringBuffer.
    public void clear(StringBuffer s) {
        s.setLength(0);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
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

    public void setFrameStack(Stack<Frame> frameStack) {
        this.frameStack = frameStack;
    }

    public StringBuffer getMessageBuffer() {
        return messageBuffer;
    }

    public void setMessageBuffer(StringBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public boolean isKeepConnected() {
        return keepConnected;
    }

    public void setKeepConnected(boolean keepConnected) {
        this.keepConnected = keepConnected;
    }
}
