package com.jasper.model.request;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.PostState;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.uriparser.RequestUriParser;

import static com.jasper.model.httpenums.State.ERROR;
import static com.jasper.model.httpenums.StatusCode.BAD_REQUEST;

/**
 * Created by jasper wil.lankhorst on 12-3-2017.
 */
public class RequestParser {

    private PostState postState = PostState.READ_POST_NAME;

    private HttpRequest request = new HttpRequest();
    private RequestUriParser requestUriParser = new RequestUriParser();
    private StringBuilder stateUrlBuilder = new StringBuilder();
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
            request.getStateBuilder().append(c);
        } else if (bufferCheck.hasSpace()) {

            request.setState(State.READ_URI);

            try {
                request.setRequestMethod(RequestType.valueOf(request.getStateBuilder().toString()));
            } catch (IllegalArgumentException ex) {
                request.setStatusCode(BAD_REQUEST);
                request.setState(ERROR);
            }

            request.getStateBuilder().setLength(0); //re-use builder.
        }
    }

    private void readUri(char c) {

        if (bufferCheck.hasSpace()) {
            requestUriParser.parseUri(request, bufferCheck, stateUrlBuilder);
            request.setState(State.READ_HTTP);
            request.getStateBuilder().setLength(0);

            if (request.getPath() == null) {
                request.setState(ERROR);
            }
        } else {

            String validUricharactersInGeneral = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()" +
                    "*+,;=`.";

            //Check if character is valid in URI, general check.
            if (validUricharactersInGeneral.contains(Character.toString(c))) {
                request.getStateBuilder().append(c);
                stateUrlBuilder.append(c);
                requestUriParser.parseUri(request, bufferCheck, stateUrlBuilder);
            } else {
                request.setState(ERROR);
                request.setStatusCode(BAD_REQUEST);
            }
        }
    }

    private void readHttp(char c) {
        if (bufferCheck.hasSpace() || bufferCheck.hasNewline()) {
            request.setState(State.READ_HEADER_NAME);
            parseHttp(request.getStateBuilder().toString());
            request.getStateBuilder().setLength(0);
        } else {
            request.getStateBuilder().append(c);
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

    private void readHeaderName(char c) {
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
    }

    private void readHeaderValue(char c) {
        request.getStateBuilder().append(c);
        //new line found.
        if (bufferCheck.hasNewline()) {
            String headerValue = request.getStateBuilder().toString();
            if (headerName != null) {
                request.addHeader(headerName, headerValue);
            }
            request.getStateBuilder().setLength(0);
            request.setState(State.READ_HEADER_NAME);
        }
    }

    //TODO cutup in smaller functions.
    private void readBody(char c) {
        request.getStateBuilder().append(c);
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
    }

    public HttpRequest getRequest() {
        return request;
    }
}
