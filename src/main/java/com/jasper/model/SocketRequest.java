package com.jasper.model;

import com.jasper.model.socket.models.entity.Frame;

import java.net.Socket;
import java.util.Stack;

public class SocketRequest extends Request {

    private Stack<Frame> frameStack = new Stack<>();
    private StringBuffer messageBuffer = new StringBuffer();

    public SocketRequest(Socket socket) {
        super(socket);
    }

    public Stack<Frame> getFrameStack() {
        return frameStack;
    }

    public StringBuffer getMessageBuffer() {
        return messageBuffer;
    }

    public void setMessageBuffer(StringBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public void clear(StringBuffer s) {
        s.setLength(0);
    }
}
