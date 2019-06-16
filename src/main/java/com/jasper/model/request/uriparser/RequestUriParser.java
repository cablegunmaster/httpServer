package com.jasper.model.request.uriparser;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.Protocol;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.BufferCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jasper.model.httpenums.State.ERROR;
import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;

public class RequestUriParser {

    private final static Logger LOG = LoggerFactory.getLogger(RequestUriParser.class);

    private String getQueryKey = null;

    //TODO needs to be refactored to be easier to read.

    /**
     * https://www.ietf.org/rfc/rfc3986.txt Supports for now a simplified version of the RFC. Not included IPV6. InputString it only the
     * URI, should check if it has invalid characters in it. TODO entity checking %20 spaces and extra entitys.
     *
     * @param request the input of the String.
     */
    public void parseUri(HttpRequest request,
                         BufferCheck bufferCheck,
                         StringBuilder stateUrlBuilder,
                         StringBuilder stateBuilder) {
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
                        LOG.info("ERROR: stateUrl: {} exception: {}", request.getStateUrl(), ex);
                        request.setState(ERROR);
                        request.setStatusCode(BAD_REQUEST); //400 if its a wrong request.
                    }
                }

                //relative url.
                if (stateUrlBuilder.toString().startsWith("/")) {
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
                    stateBuilder.append("/"); //add the forward slash.
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
                            LOG.info("ERROR: reading PORT state:{}", request.getStateUrl().name());
                            request.setStatusCode(BAD_REQUEST);
                            request.setState(ERROR);
                        }

                        stateUrlBuilder.setLength(0); //re-use builder.
                        stateBuilder.append("/"); //add the forward slash.

                        request.setStateUrl(StateUrl.READ_PATH);
                    } catch (IllegalArgumentException ex) {
                        LOG.info("ERROR: stateUrl: {} exception: {}", request.getStateUrl(), ex);
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
                checkEndOfLineOrHasHash(request, bufferCheck, stateUrlBuilder);

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

                //End of line or hash
                checkEndOfLineOrHasHash(request, bufferCheck, stateUrlBuilder);

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

        //TODO check Delimiter ? & ; for query parameter.

        //most likely urls who are longer as 255 chars are invalid.
        if (stateBuilder.length() > 255) {
            LOG.info("ERROR: reading url: {} ,  state:{}", stateBuilder.toString(), request.getStateUrl().name());
            request.setStatusCode(StatusCode.URI_TOO_LONG);
            request.setState(ERROR); //414 URI Too Long
        }
    }


    /**
     * Checks if the buffer has a space or has Hash in the buffer.
     * Sets the state to read "Fragment" part of the url.
     *
     * @param request
     * @param bufferCheck
     * @param stateUrlBuilder
     */
    private void checkEndOfLineOrHasHash(HttpRequest request,
                                         BufferCheck bufferCheck,
                                         StringBuilder stateUrlBuilder) {

        if (bufferCheck.hasSpace() || bufferCheck.hasHash()) {
            request.setFilename(request.getPath() + request.getQuery()); //combine everything.
            stateUrlBuilder.setLength(0); //re-use builder.

            if (bufferCheck.hasHash()) {
                request.setStateUrl(StateUrl.READ_FRAGMENT);
            }
        }
    }
}
