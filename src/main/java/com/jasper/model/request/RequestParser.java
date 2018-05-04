package com.jasper.model.request;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.PostState;
import com.jasper.model.httpenums.Protocol;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;

import static com.jasper.model.httpenums.State.ERROR;
import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private StringBuilder stateUrlBuilder = new StringBuilder();
    private BufferCheck bufferCheck = new BufferCheck();
    private HttpRequest request = new HttpRequest();
    private PostState postState = PostState.READ_POST_NAME;

    private final static Integer BUFFER_SIZE_CACHE = 8192;
    private int bufferSize = 0;

    private String headerName = null;
    private String headerValue = null;
    private String getQueryKey = null;
    private String queryValue = null;

    private String postQueryKey = null;
    private String postQueryValue = null;
    private int postSize = 0;
    Integer totalBodySize = 0;

    /**
     * Input to be processed and checked.
     */
    public void nextCharacter(char c) {

        bufferCheck.addToBuffer(c);
        bufferSize++;

        if (bufferSize > BUFFER_SIZE_CACHE) {
            request.setStatusCode(StatusCode.PAYLOAD_TO_LARGE);
            request.setState(ERROR);//413
            return;
        }

        switch (request.getState()) {
            case READ_METHOD:
                if (bufferCheck.hasSpace()) {
                    request.setState(State.READ_URI);
                    readMethod(request.getStateBuilder().toString());
                    request.getStateBuilder().setLength(0); //re-use builder.
                } else {
                    request.getStateBuilder().append(c);
                }
                break;
            case READ_URI:
                if (bufferCheck.hasSpace()) {
                    readUri(request);
                    request.setState(State.READ_HTTP);
                    request.getStateBuilder().setLength(0);
                    if (request.getPath() == null) {
                        request.setState(ERROR);
                    }
                } else {
                    String validUricharactersInGeneral = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()" +
                            "*+,;=`.";
                    if(validUricharactersInGeneral.contains(Character.toString(c))) {
                        request.getStateBuilder().append(c);
                        stateUrlBuilder.append(c);
                        readUri(request);
                    }else{
                        request.setState(ERROR);
                        request.setStatusCode(BAD_REQUEST);
                    }
                }
                break;
            case READ_HTTP:
                if (bufferCheck.hasSpace() || bufferCheck.hasNewline()) {
                    request.setState(State.READ_HEADER_NAME);
                    readHTTP(request.getStateBuilder().toString());
                    request.getStateBuilder().setLength(0);
                } else {
                    request.getStateBuilder().append(c);
                }
                break;
            case READ_HEADER_NAME:
                request.getStateBuilder().append(c);

                //found ":"
                if (bufferCheck.hasSemiColon()) {
                    request.setState(State.READ_HEADER_VALUE);
                    headerName = request.getStateBuilder().toString();
                    request.getStateBuilder().setLength(0);
                }

                //found /r/n request parsing DONE
                if (bufferCheck.hasNewline()) {
                    request.getStateBuilder().setLength(0);

                    if (request.getRequestMethod().isGetRequest()) {
                        request.setState(State.DONE);
                    } else if (request.getRequestMethod().ispostRequest()) {
                        if (request.getHeaders().containsKey("Content-Length")) {
                            request.setState(State.READ_BODY);
                        } else {
                            request.setState(ERROR);
                            request.setStatusCode(StatusCode.LENGTH_REQUIRED);
                        }
                    }
                }
                break;
            case READ_HEADER_VALUE:
                request.getStateBuilder().append(c);
                //new line found.
                if (bufferCheck.hasNewline()) {
                    headerValue = request.getStateBuilder().toString();
                    if (headerName != null) {
                        request.addHeader(headerName, headerValue);
                    }
                    request.getStateBuilder().setLength(0);
                    request.setState(State.READ_HEADER_NAME);
                }
                break;
            case READ_BODY:
                //TODO needs to be helped with CONTENT-length?
                request.getStateBuilder().append(c);
                postSize++;

                if (totalBodySize == 0) {
                    if (request.getHeaders().get("Content-Length") == null) {
                        request.setState(ERROR);
                        request.setStatusCode(StatusCode.LENGTH_REQUIRED);
                    } else {
                        String bodySizeString = request.getHeaders().get("Content-Length");
                        if (bodySizeString != null) {
                            totalBodySize = Integer.parseInt(bodySizeString);
                        }
                    }
                }


                if (postSize < totalBodySize) {

                    if (postState.isPostValue()) {
                        if (bufferCheck.hasDelimiter()) {
                            postQueryValue = request.getStateBuilder().toString();
                            postState = PostState.READ_POST_NAME;
                            request.getQueryPOST().put(postQueryKey, postQueryValue);
                            request.getStateBuilder().setLength(0);
                        }
                    }

                    if (postState.isPostName()) {
                        if (bufferCheck.hasEqualsymbol()) {
                            postQueryKey = request.getStateBuilder().toString();
                            postState = PostState.READ_POST_VALUE;
                            request.getStateBuilder().setLength(0);
                        }
                    }

                } else if (postSize == totalBodySize) {
                    postQueryValue = request.getStateBuilder().toString();
                    request.getQueryPOST().put(postQueryKey, postQueryValue);
                    request.getStateBuilder().setLength(0);
                    request.setState(State.DONE);
                } else if (postSize > totalBodySize) {
                    request.setStatusCode(StatusCode.PAYLOAD_TO_LARGE);
                    request.setState(ERROR);
                }


                break;
            case ERROR:
                //Stop reading, cancel further working on it.
                break;
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
            String version = split[1].substring(0, 3);

            if (version.equals("1.1") || version.equals("1.0")) {
                try {
                    request.setHttpVersion(Double.parseDouble(version));
                    validHttp = true;
                } catch (NumberFormatException ex) {
                    request.setStatusCode(StatusCode.HTTP_VERSION_NOT_SUPPORTED);
                }
            }
        }

        if (!validHttp) {
            request.setState(ERROR);
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
                if (bufferCheck.hasDoubleForwardSlash()) {
                    try {
                        input = stateUrlBuilder.toString();
                        stateUrlBuilder.setLength(0); //re-use builder.
                        request.setProtocol(Protocol.valueOf(input.substring(0, input.length() - 3).toUpperCase())); //minus "://" symbol.

                        request.setStateUrl(StateUrl.READ_AUTHORITY);
                    } catch (IllegalArgumentException ex) {
                        request.setState(ERROR);
                        request.setStatusCode(BAD_REQUEST); //400 if its a wrong request.
                    }
                }

                //relative url.
                if (startWithForwardSlashInBuilder()) {
                    //Relative uses basic HTTP. (input from .htaccess file if https / http should be used)
                    request.setProtocol(Protocol.HTTP);
                    stateUrlBuilder.setLength(0); //re-use builder.
                    request.setStateUrl(StateUrl.READ_PATH);
                }
                break;
            case READ_AUTHORITY:
                //check if it has a port.
                if (bufferCheck.hasSemiColon()) {
                    input = stateUrlBuilder.toString(); //no builder flush.
                    request.setHost(input.substring(0, input.length() - 1)); //everything minus ':'

                    request.setStateUrl(StateUrl.READ_PORT);
                }

                if (bufferCheck.hasForwardSlash()) {
                    input = stateUrlBuilder.toString(); //no builder flush.

                    if (!input.equals("")) {
                        request.setHost(input.substring(0, input.length() - 1)); //everything minus '/'
                    }

                    stateUrlBuilder.setLength(0); //re-use builder.
                    request.getStateBuilder().append("/"); //add the forward slash.
                    request.setStateUrl(StateUrl.READ_PATH);
                }
                break;
            case READ_PORT:
                if (bufferCheck.hasForwardSlash()) {

                    try {
                        input = stateUrlBuilder.toString();
                        input = input.replace(request.getHost() + ":", ""); //remove host.
                        int portNumber = Integer.parseInt(input.substring(0, input.length() - 1));

                        if (portNumber >= 80) {
                            request.setPort(portNumber); //everything minus "/"
                        } else {
                            request.setStatusCode(BAD_REQUEST);
                            request.setState(ERROR);
                        }

                        stateUrlBuilder.setLength(0); //re-use builder.
                        request.getStateBuilder().append("/"); //add the forward slash.

                        request.setStateUrl(StateUrl.READ_PATH);
                    } catch (IllegalArgumentException ex) {
                        request.setStatusCode(BAD_REQUEST); //400 if its a wrong request.
                        request.setState(ERROR);
                    }
                }
                break;
            case READ_PATH:
                if (bufferCheck.hasHash() || bufferCheck.hasQuestionMark() || bufferCheck.hasSpace()) {
                    request.setPath("/" + stateUrlBuilder.toString());
                    stateUrlBuilder.setLength(0); //re-use builder.

                    if (bufferCheck.hasHash()) {
                        request.setStateUrl(StateUrl.READ_FRAGMENT);
                    }

                    if (bufferCheck.hasQuestionMark()) {
                        request.setStateUrl(StateUrl.READ_QUERY_NAME);
                    }
                }
                break;
            case READ_QUERY_NAME:
                if (bufferCheck.hasEqualsymbol()) {
                    getQueryKey = stateUrlBuilder.toString();
                    stateUrlBuilder.setLength(0); //re-use builder.
                    request.setStateUrl(StateUrl.READ_QUERY_VALUE);
                }

                //End of line or Hash
                if (bufferCheck.hasSpace() || bufferCheck.hasHash()) {
                    request.setFilename(request.getPath() + request.getQuery()); //combine everything.
                    stateUrlBuilder.setLength(0); //re-use builder.

                    if (bufferCheck.hasHash()) {
                        request.setStateUrl(StateUrl.READ_FRAGMENT);
                    }
                }

                break;
            case READ_QUERY_VALUE:
                if (bufferCheck.hasDelimiter() || bufferCheck.hasSpace()) {
                    if (getQueryKey != null) {
                        input = stateUrlBuilder.toString();
                        request.getQueryGET().get(getQueryKey);
                        request.getQueryGET().put(getQueryKey, input);

                        request.setQuery(request.getQuery() == null
                                ? getQueryKey + input :
                                request.getQuery() + getQueryKey + input);
                    }

                    stateUrlBuilder.setLength(0); //re-use builder.
                    request.setStateUrl(StateUrl.READ_QUERY_NAME);
                }

                if (bufferCheck.hasSpace() || bufferCheck.hasHash()) {
                    request.setFilename(request.getPath() + request.getQuery()); //combine everything.
                    stateUrlBuilder.setLength(0); //re-use builder.

                    if (bufferCheck.hasHash()) {
                        request.setStateUrl(StateUrl.READ_FRAGMENT);
                    }
                }

                break;
            case READ_FRAGMENT:
                if (bufferCheck.hasSpace()) {
                    input = stateUrlBuilder.toString();
                    request.setRef(input);
                    stateUrlBuilder.setLength(0); //re-use builder.
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
            request.setState(ERROR); //414 URI Too Long
        }
    }


    public HttpRequest getRequest() {
        return request;
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
            request.setStatusCode(BAD_REQUEST);
            request.setState(ERROR);
        }
    }

    private boolean startWithForwardSlashInBuilder() {
        return stateUrlBuilder.toString().startsWith("/");
    }

    private void readQueryEndOfLineOrHashState() {
        //End of line or Hash
    }
}
