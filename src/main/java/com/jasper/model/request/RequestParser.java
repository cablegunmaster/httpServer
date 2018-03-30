package com.jasper.model.request;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.Protocol;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private HttpRequest request = new HttpRequest();

    private char[] buffer = new char[4];
    private int bufferIndex = -1;
    private final static Integer BUFFER_SIZE_CACHE = 8192;
    private StringBuilder stateUrlBuilder = new StringBuilder();
    private int bufferSize = 0;
    private String queryKey = null;
    private String queryValue = null;

    public RequestParser() {
    }

    /**
     * Input to be processed and checked.
     */
    public void nextCharacter(char c) {

        bufferIndex++; //Size of request.
        bufferIndex = bufferIndex % buffer.length;
        buffer[bufferIndex] = c;
        bufferSize++;

        if (bufferSize > BUFFER_SIZE_CACHE) {
            request.setStatusCode(StatusCode.PAYLOAD_TO_LARGE); //413
            return;
        }

        switch (request.getState()) {
            case READ_METHOD:
                if (hasSpace()) {
                    request.setState(State.READ_URI);
                    readMethod(request.getStateBuilder().toString());
                    request.getStateBuilder().setLength(0); //re-use builder.
                } else {
                    request.getStateBuilder().append(c);
                }
                break;
            case READ_URI:
                if (hasSpace()) {
                    readUri(request);

                    request.setState(State.READ_HTTP);
                    request.getStateBuilder().setLength(0);

                    if (request.getPath() == null) {
                        request.setState(State.ERROR);
                    }

                } else {
                    request.getStateBuilder().append(c);
                    getStateUrlBuilder().append(c);
                    readUri(request);
                }
                break;
            case READ_HTTP:
                if (hasSpace()) {
                    request.setState(State.READ_HEADER_NAME);
                    readHTTP(request.getStateBuilder().toString());
                    request.getStateBuilder().setLength(0);
                } else {
                    request.getStateBuilder().append(c);
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
     * TODO entity checking %20 spaces and extra entitys.
     *
     * @param request the input of the String.
     */
    private void readUri(HttpRequest request) {

        String input;

        switch (request.getStateUrl()) {
            case READ_PROTOCOL:
                //absolute URL.
                if (hasDoubleForwardSlash()) {
                    try {
                        input = getStateUrlBuilder().toString();
                        getStateUrlBuilder().setLength(0); //re-use builder.
                        request.setProtocol(Protocol.valueOf(input.substring(0, input.length() - 3).toUpperCase())); //minus "://" symbol.

                        request.setStateUrl(StateUrl.READ_AUTHORITY);
                    } catch (IllegalArgumentException ex) {
                        request.setState(State.ERROR);
                        request.setStatusCode(StatusCode.BAD_REQUEST); //400 if its a wrong request.
                    }
                }

                //relative url.
                if (startWithForwardSlashInBuilder()) {
                    //Relative uses basic HTTP. (input from .htaccess file if https / http should be used)
                    request.setProtocol(Protocol.HTTP);
                    getStateUrlBuilder().setLength(0); //re-use builder.
                    request.setStateUrl(StateUrl.READ_PATH);
                }
                break;
            case READ_AUTHORITY:

                //check if it has a port.
                if (hasSemiColon()) {
                    input = getStateUrlBuilder().toString(); //no builder flush.
                    request.setHost(input.substring(0, input.length() - 1)); //everything minus ':'

                    request.setStateUrl(StateUrl.READ_PORT);
                }

                if (hasForwardSlash()) {
                    input = getStateUrlBuilder().toString(); //no builder flush.

                    if (!input.equals("")) {
                        request.setHost(input.substring(0, input.length() - 1)); //everything minus '/'
                    }

                    getStateUrlBuilder().setLength(0); //re-use builder.
                    request.getStateBuilder().append("/"); //add the forward slash.
                    request.setStateUrl(StateUrl.READ_PATH);
                }
                break;
            case READ_PORT:
                if (hasForwardSlash()) {

                    try {
                        input = getStateUrlBuilder().toString();
                        input = input.replace(request.getHost() + ":", ""); //remove host.
                        int portNumber = Integer.parseInt(input.substring(0, input.length() - 1));

                        if (portNumber >= 80) {
                            request.setPort(portNumber); //everything minus "/"
                        } else {
                            request.setStatusCode(StatusCode.BAD_REQUEST);
                            request.setState(State.ERROR);
                        }

                        getStateUrlBuilder().setLength(0); //re-use builder.
                        request.getStateBuilder().append("/"); //add the forward slash.

                        request.setStateUrl(StateUrl.READ_PATH);
                    } catch (IllegalArgumentException ex) {
                        request.setStatusCode(StatusCode.BAD_REQUEST); //400 if its a wrong request.
                        request.setState(State.ERROR);
                    }
                }
                break;
            case READ_PATH:
                if (hasHash() || hasQuestionMark() || hasSpace()) {

                    input = getStateUrlBuilder().toString();

                    if (input.equals("")) {
                        input = "/"; //relative path fix?
                    }

                    request.setPath(input);
                    getStateUrlBuilder().setLength(0); //re-use builder.

                    if (hasHash()) {
                        request.setStateUrl(StateUrl.READ_FRAGMENT);
                    }

                    if (hasQuestionMark()) {
                        request.setStateUrl(StateUrl.READ_QUERY_KEY);
                    }
                }
                break;
            case READ_QUERY_KEY:
                if (hasEqualsymbol()) {
                    queryKey = getStateUrlBuilder().toString();
                    request.getQueryValues().put(queryKey, "");
                    getStateUrlBuilder().setLength(0); //re-use builder.
                    request.setStateUrl(StateUrl.READ_QUERY_VALUE);
                }

                //End of line or Hash
                readQueryEndOfLineOrHashState();
                break;
            case READ_QUERY_VALUE:
                if (hasDelimiter() || hasSpace()) {
                    if (queryKey != null) {
                        input = getStateUrlBuilder().toString();
                        request.getQueryValues().get(queryKey);
                        request.getQueryValues().put(queryKey, input);

                        if (request.getQuery() == null) {
                            request.setQuery(queryKey + input);
                        }
                    }

                    getStateUrlBuilder().setLength(0); //re-use builder.
                    request.setStateUrl(StateUrl.READ_QUERY_KEY);
                }
                readQueryEndOfLineOrHashState();
                break;
            case READ_FRAGMENT:
                if (hasSpace()) {
                    input = getStateUrlBuilder().toString();
                    request.setRef(input);
                    getStateUrlBuilder().setLength(0); //re-use builder.
                }
                break;
        }


        //TODO make entity valid, and conform to some basic rules.
        //Basically recreating URL class.
        // https://docs.oracle.com/javase/tutorial/networking/urls/urlInfo.html
        //make a check for HTTP , HTTPS,

        // Help with which characters may or may not appear and when unencoded or should be encoded:
        //https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#Generic_syntax


        //Delimiter ? & ; for query parameter.

        //most likely urls who are longer as 255 chars are invalid.
        if (request.getStateBuilder().length() > 255) {
            request.setStatusCode(StatusCode.URI_TOO_LONG);
            request.setState(State.ERROR); //414 URI Too Long
        }
//
        //ULR invalid 404 statuscode.
    }

    private boolean startWithForwardSlashInBuilder() {
        return getStateUrlBuilder().toString().startsWith("/");
    }

    public void readQueryEndOfLineOrHashState() {
        //End of line or Hash
        if (hasSpace() || hasHash()) {
            request.setFilename(request.getPath() + request.getQuery()); //combine everything.
            getStateUrlBuilder().setLength(0); //re-use builder.

            if (hasHash()) {
                request.setStateUrl(StateUrl.READ_FRAGMENT);
            }
        }
    }

    public StringBuilder getStateUrlBuilder() {
        return stateUrlBuilder;
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

    private boolean hasHash() {
        return buffer[bufferIndex] == '#';
    }

    private boolean hasQuestionMark() {
        return buffer[bufferIndex] == '?';
    }

    private boolean hasEqualsymbol() {
        return buffer[bufferIndex] == '=';
    }

    private boolean hasDelimiter() {
        return buffer[bufferIndex] == '&' || buffer[bufferIndex] == ';';
    }
}
