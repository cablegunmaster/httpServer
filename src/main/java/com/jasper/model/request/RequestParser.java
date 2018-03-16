package com.jasper.model.request;

import com.jasper.model.request.requestenums.RequestType;
import com.jasper.model.request.requestenums.State;
import com.jasper.model.request.requestenums.StatusCode;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private HttpRequest request = new HttpRequest();
    private char[] buffer = new char[4];
    private int bufferIndex;

    public RequestParser() {
    }

    /**
     * Input to be processed and checked.
     */
    public void nextCharacter(char c) {

        buffer[bufferIndex] = c;
        bufferIndex = bufferIndex % buffer.length;

        //Parameter variable length of request. 7K
        //413 Entity too large
        if (bufferIndex > 8192) {
            request.setStatusCode(StatusCode.PAYLOAD_TO_LARGE);
            return;
        }

        switch (request.getState()) {
            case READ_METHOD:
                if (hasSpace()) {
                    request.setState(State.READ_URI);
                    readMethod(request.getMethod().toString());
                    request.getMethod().setLength(0); //clean builder.
                } else {
                    request.getMethod().append(c);
                }
                break;
            case READ_URI:
                if (hasSpace()) {
                    request.setState(State.READ_HTTP);
                    readUri(request.getMethod().toString());
                    request.getMethod().setLength(0);
                } else {
                    request.getMethod().append(c);
                }
                break;
            case READ_HTTP:
                if (hasSpace()) {
                    request.setState(State.READ_HEADER_NAME);
                    readHTTP(request.getMethod().toString());
                    request.getMethod().setLength(0);
                } else {
                    request.getMethod().append(c);
                }
                break;
            case READ_HEADER_NAME:
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
                break;
            case ERROR:
                //Stop reading, cancel further working on it.
        }
    }

    /**
     * HTTP/1.1 or HTTP/2.0 or HTTP/1.0 or HTTP/0.9
     * Consists of HTTP[/]MajorVersion.Minorversion.
     *
     * @param inputString the third space from the httpVersion.
     */
    private void readHTTP(String inputString) {
        //check the HTTP  forward slash , and major minor is a number with no front zero.
        boolean validHttp = false;

        if (inputString.startsWith("HTTP/")) {
            String[] split = inputString.split("/", 2);
            String version = split[1];

            if (version != null && (version.equals("1.1") || version.equals("1.0"))) {
                try {
                    request.setHttpVersion(Long.parseLong(version));
                    validHttp = true;
                } catch (NumberFormatException ex) {
                    validHttp = false;
                    request.setStatusCode(StatusCode.HTTP_VERSION_NOT_SUPPORTED);
                }
            }
        }

        if (!validHttp) {
            request.setState(State.ERROR);
        }
    }

    /**
     * https://www.ietf.org/rfc/rfc3986.txt Supports for now a simplified version of the RFC.
     * Not included IPV6.
     * InputString it only the URI, should check if it has invalid characters in it.
     *
     * @param uri the input of the String.
     */
    private void readUri(String uri) {

        //TODO make entity valid, and conform to some basic rules.

        //make a check for HTTP , HTTPS,
        if (uri.startsWith("/") || uri.startsWith("HTTP") || uri.startsWith("HTTPS")) {
            request.setRequestpath(uri);
        }

        //most likely urls who are longer as 255 chars are invalid.
        if (uri.length() > 255) {
            request.setStatusCode(StatusCode.URI_TOO_LONG);
            request.setState(State.ERROR); //414 URI Too Long
        }
    }

    /**
     * Once its found to be in a RequestType
     * Checks if its a valid RequestType.
     *
     * @param inputString first part of the input String.
     */
    private void readMethod(String inputString) {
        try {
            request.setRequestMethod(RequestType.valueOf(inputString));
        } catch (IllegalArgumentException ex) {
            request.setStatusCode(StatusCode.BAD_REQUEST);
            request.setState(State.ERROR);
        }
    }

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

    public HttpRequest getRequest() {
        return request;
    }
}
