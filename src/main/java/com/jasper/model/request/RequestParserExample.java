package com.jasper.model.request;

import java.util.HashMap;
import java.util.Map;

public class RequestParserExample {

    private enum Status {
        READ_METHOD,
        READ_URI,
        READ_HTTP,
        READ_HEADER_NAME,
        READ_HEADER_VALUE,
        READ_BODY,
        DONE,
        ERROR,
        ;
    }

    public class Request {
        private Status status = Status.READ_METHOD;
        private StringBuilder method = new StringBuilder();
        private Map<String, String> headers = new HashMap<>();

        private StringBuilder headerName = new StringBuilder();
        private StringBuilder headerValue = new StringBuilder();
    }

    private Request request = new Request();
    private char[] buffer = new char[4];
    private int bufferIndex;

    private boolean hasSpace() {
        return buffer[bufferIndex] == ' ';
    }

    private boolean hasNewline() {
        return buffer[bufferIndex] == '\n' && buffer[(bufferIndex + 3) % 4] == '\r';
    }

    private boolean hasDoubleNewline() {
        return buffer[bufferIndex] == '\n' && buffer[(bufferIndex + 3) % 4] == '\r' &&
                buffer[(bufferIndex + 2) % 4] == '\n' && buffer[(bufferIndex + 1) % 4] == '\r';
    }

    public void nextCharacter(char c) {
        // check length
        // buffer remove first, buffer.append(c)
        buffer[bufferIndex] = c;
        bufferIndex = bufferIndex % buffer.length;

        switch(request.status) {
            case READ_METHOD:
                if (hasSpace()) {
                    request.status = Status.READ_URI;
                } else {
                    request.method.append(c);
                }
            case READ_URI:
                break;
            case READ_HEADER_VALUE:
                if (hasNewline()) {
                    if (hasDoubleNewline()) {
                        // end headers
                        // -> DONE
                    } else {
                        // end header
                        // -> READ_HEADER_NAME
                    }
                }
        }
    }

    public static void main(String[] args) {
        RequestParserExample example = new RequestParserExample();

        example.nextCharacter('G');
        example.nextCharacter('E');
        example.nextCharacter('T');
        example.nextCharacter(' ');
    }

}

