package com.jasper.model.request;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.PostState;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.uriparser.RequestUriParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;

import static com.jasper.model.httpenums.State.ERROR;
import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;
import static com.jasper.model.httpenums.StatusCode.SWITCHING_PROTOCOL;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private PostState postState = PostState.READ_POST_NAME;

    private HttpRequest request = new HttpRequest();
    private RequestUriParser requestUriParser = new RequestUriParser();
    private StringBuilder stateUrlBuilder = new StringBuilder();
    private StringBuilder stateBuilder = new StringBuilder();
    private BufferCheck bufferCheck = new BufferCheck();

    private final static Integer BUFFER_SIZE_CACHE = 8192;
    private String headerName = null;
    private String postQueryKey = null;
    private int bufferSize = 0;
    private int postSize;
    private int totalBodySize;

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
                readMethod(c);
                break;
            case READ_URI:
                readUri(c);
                break;
            case READ_HTTP:
                readHttp(c);
                break;
            case READ_HEADER_NAME:
                readHeaderName(c);
                break;
            case READ_HEADER_VALUE:
                readHeaderValue(c);
                break;
            case READ_BODY:
                readBody(c);
                break;
            case ERROR:
                //Stop reading, cancel further working on it.
                break;
        }
    }

    /**
     * Read Method
     *
     * @param c character of the request.
     */
    private void readMethod(char c) {

        if (!bufferCheck.hasSpace()) {
            stateBuilder.append(c);
        } else if (bufferCheck.hasSpace()) {

            request.setState(State.READ_URI);

            try {
                request.setRequestMethod(RequestType.valueOf(stateBuilder.toString()));
            } catch (IllegalArgumentException ex) {
                request.setStatusCode(BAD_REQUEST);
                request.setState(ERROR);
            }

            stateBuilder.setLength(0); //re-use builder.
        }
    }

    private void readUri(char c) {

        if (bufferCheck.hasSpace()) {
            requestUriParser.parseUri(request, bufferCheck, stateUrlBuilder, stateBuilder);
            request.setState(State.READ_HTTP);
            stateBuilder.setLength(0);

            if (request.getPath() == null) {
                request.setState(ERROR);
            }
        } else {

            String validUricharactersInGeneral = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()" +
                    "*+,;=`.";

            //Check if character is valid in URI, general check.
            if (validUricharactersInGeneral.contains(Character.toString(c))) {
                stateBuilder.append(c);
                stateUrlBuilder.append(c);
                requestUriParser.parseUri(request, bufferCheck, stateUrlBuilder, stateBuilder);
            } else {
                request.setState(ERROR);
                request.setStatusCode(BAD_REQUEST);
            }
        }
    }

    private void readHttp(char c) {
        if (bufferCheck.hasSpace() || bufferCheck.hasNewline()) {
            request.setState(State.READ_HEADER_NAME);
            parseHttp(stateBuilder.toString());
            stateBuilder.setLength(0);
        } else {
            stateBuilder.append(c);
        }
    }


    /**
     * HTTP/1.1 or HTTP/2.0 or HTTP/1.0 or HTTP/0.9 Consists of HTTP[/]MajorVersion.Minorversion.
     *
     * @param inputString the third space from the httpVersion.
     */
    private void parseHttp(String inputString) {
        //check the HTTP  forward slash , and major minor is a number with no front zero.
        boolean validHttp = false;

        if (inputString.startsWith("HTTP/")) {
            String[] split = inputString.split("/", 2);
            String version = split[1].substring(0, 3);

            if (version.equals("1.1") || version.equals("1.0")) {
                try {
                    request.setHttpVersion(version);
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

    private void readHeaderName(char c) {
        stateBuilder.append(c);
        //found ":"
        if (bufferCheck.hasSemiColon()) {
            request.setState(State.READ_HEADER_VALUE);
            headerName = stateBuilder.toString();
            stateBuilder.setLength(0);
        }

        //found /r/n request parsing DONE
        if (bufferCheck.hasNewline()) {
            stateBuilder.setLength(0);

            if (request.getRequestMethod() != null)
                switch (request.getRequestMethod()) {
                    case POST:
                        Map<String, String> headers = request.getHeaders();
                        if (headers.containsKey("Content-Length")) {
                            request.setState(State.READ_BODY);
                        } else {
                            request.setState(ERROR);
                            request.setStatusCode(StatusCode.LENGTH_REQUIRED);
                        }
                        break;
                    case GET:
                        bufferSize = 0;
                        request.setState(State.DONE);
                        break;
                    default:
                        request.setState(ERROR);
                        break;
                }
        }
    }


    /**
     * All functions regarding "special" header value / names should be dealt here.
     *
     * @param c
     */
    private void readHeaderValue(char c) {
        stateBuilder.append(c);
        //new line found.
        if (bufferCheck.hasNewline()) {

            //Function read all the header values.
            //Special way of dealing with all the different headers function needs to be here.

            String headerValue = removeRN(stateBuilder.toString());
            if (headerName != null) {
                headerName = headerName.replace(":", "");
                switch (headerName) {
                    case "Upgrade":
                        request.setUpgradingConnection(true);
                        request.setStatusCode(StatusCode.SWITCHING_PROTOCOL);
                        break;
                    case "Sec-WebSocket-Key":
                        request.setStatusCode(SWITCHING_PROTOCOL);
                        request.setUpgradeSecureKeyAnswer(encodeWebsocketAccept(headerValue));
                        break;
                    default:
                        break;
                }

                request.addHeader(headerName, headerValue.replaceAll("\n", ""));
            }
            stateBuilder.setLength(0);
            request.setState(State.READ_HEADER_NAME);
        }
    }

    //TODO cutup in smaller functions.
    private void readBody(char c) {
        stateBuilder.append(c);
        postSize++;

        if (totalBodySize == 0) {

            //if header value is empty.
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

        String postQueryValue;
        if (postSize < totalBodySize) {
            if (postState.isPostValue()) {
                if (bufferCheck.hasDelimiter()) {
                    postQueryValue = stateBuilder.toString();
                    postState = PostState.READ_POST_NAME;
                    request.getQueryPOST().put(postQueryKey, postQueryValue);
                    stateBuilder.setLength(0);
                }
            }

            if (postState.isPostName()) {
                if (bufferCheck.hasEqualsymbol()) {
                    postQueryKey = stateBuilder.toString();
                    postState = PostState.READ_POST_VALUE;
                    stateBuilder.setLength(0);
                }
            }

        } else if (postSize == totalBodySize) {

            postQueryValue = stateBuilder.toString();
            request.getQueryPOST().put(postQueryKey, postQueryValue);
            stateBuilder.setLength(0);
            bufferSize = 0;
            request.setState(State.DONE);

        } else if (postSize > totalBodySize) {
            request.setStatusCode(StatusCode.PAYLOAD_TO_LARGE);
            request.setState(ERROR);
        }
    }

    public HttpRequest getRequest() {
        return request;
    }

    private String removeRN(String inputString) {
        return inputString.replaceAll("(\r\n|\n)", "");
    }


    /**
     * https://en.wikipedia.org/wiki/WebSocket
     *
     * @param input
     * @return the string.
     */
    public String encodeWebsocketAccept(String input) {
        return new String(Base64.encodeBase64(DigestUtils.sha1(input.trim() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));
    }
}
