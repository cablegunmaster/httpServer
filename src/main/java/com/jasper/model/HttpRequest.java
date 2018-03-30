package com.jasper.model;

import com.jasper.model.httpenums.Protocol;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Model for a request to be send / used.
 */
public class HttpRequest {

    private State state = State.READ_METHOD;
    private StringBuilder stateBuilder = new StringBuilder();
    private StatusCode statusCode = null; //status code of request.

    //Method variables.
    private RequestType requestMethod = null;    //Method GET / POST

    //URL variables.
    private StateUrl stateUrl = StateUrl.READ_PROTOCOL;
    private Protocol protocol = null;
    private String authority = null; //is host +":" +  port
    private String host = null; //name of website, minus Protocol or port
    private Integer port = 80; //80 is default port.
    private String path = null;
    private String query = null; //everything behind the question mark
    private String filename = null; //path + query.
    private String ref = null; //Bookmark with # which part of the page it should put as top.
    private Map<String,String> queryValues = new HashMap<>();

    //Headers.
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder headerName = new StringBuilder();
    private StringBuilder headerValue = new StringBuilder();

    //HttpVersion number.
    private Long httpVersion;

    public HttpRequest() {
    }

    public void setRequestMethod(RequestType requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RequestType getRequestMethod() {
        return requestMethod;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public State getState() {
        return state;
    }

    public void setState(State status) {
        this.state = status;
    }

    public void setStateUrl(StateUrl stateUrl) {
        this.stateUrl = stateUrl;
    }

    public StateUrl getStateUrl() {
        return stateUrl;
    }

    public StringBuilder getStateBuilder() {
        return stateBuilder;
    }

    public void setStateBuilder(StringBuilder stateBuilder) {
        this.stateBuilder = stateBuilder;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Long getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(Long httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setHttpVersion(long httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public StringBuilder getHeaderName() {
        return headerName;
    }

    public void setHeaderName(StringBuilder headerName) {
        this.headerName = headerName;
    }

    public StringBuilder getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(StringBuilder headerValue) {
        this.headerValue = headerValue;
    }


    /**
     * Output of the whole file, the full request as a String to be send back to the client.
     * TODO improve this toString function to be able to send back an extra support.
     */
    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        byte[] s = null;

        //TODO missing sending request numbers like 200, 404, 500 etc..
        String content =
                "<html>" +
                        "<body>" +
                        "<h1>This is á successful request</h1>" +
                        "</body>" +
                        "</html>";

        buffer.append("HTTP/1.1 200 OK\r\n");

        //read the amount of bytes;
        byte[] contentInBytes = null;

        try {
            contentInBytes = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Integer contentLength = 0;
        if (contentInBytes != null) {
            contentLength = contentInBytes.length;
        }

        buffer.append("Content-Length: ").append(contentLength).append("\r\n");
        buffer.append("Content-Type: text/html; charset=utf-8");
        buffer.append("\r\n\r\n");
        buffer.append(content);
        buffer.append("\r\n\r\n");

        return buffer.toString();
    }

    public Map<String, String> getQueryValues() {
        return queryValues;
    }

    public void setQueryValues(Map<String, String> queryValues) {
        this.queryValues = queryValues;
    }
}
