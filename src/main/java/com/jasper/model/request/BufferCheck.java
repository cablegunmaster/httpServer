package com.jasper.model.request;

public class BufferCheck {

    private char[] buffer = new char[4];
    private int bufferIndex = -1;

    public void addToBuffer(char c) {
        bufferIndex++; //Size of request.
        bufferIndex = bufferIndex % buffer.length;
        buffer[bufferIndex] = c;
    }

    public boolean hasSpace() {
        return buffer[bufferIndex] == ' ';
    }

    public boolean hasNewline() {
        return (buffer[bufferIndex] == '\n' && buffer[(bufferIndex + 3) % 4] == '\r') || buffer[bufferIndex] == '\n';
    }

    public boolean hasDoubleNewline() {
        return buffer[bufferIndex] == '\n' && buffer[(bufferIndex + 3) % 4] == '\r' &&
                buffer[(bufferIndex + 2) % 4] == '\n' && buffer[(bufferIndex + 1) % 4] == '\r';
    }

    public boolean hasForwardSlash() {
        return buffer[bufferIndex] == '/';
    }

    public boolean hasDoubleForwardSlash() {
        return buffer[bufferIndex] == '/' && buffer[(bufferIndex + 3) % 4] == '/' ||
                buffer[(bufferIndex + 2) % 4] == '/' && buffer[(bufferIndex + 1) % 4] == '/';
    }

    public boolean hasSemiColon() {
        return buffer[bufferIndex] == ':';
    }

    public boolean hasHash() {
        return buffer[bufferIndex] == '#';
    }

    public boolean hasQuestionMark() {
        return buffer[bufferIndex] == '?';
    }

    public boolean hasEqualsymbol() {
        return buffer[bufferIndex] == '=';
    }

    public boolean hasDelimiter() {
        return buffer[bufferIndex] == '&' || buffer[bufferIndex] == ';';
    }
}
