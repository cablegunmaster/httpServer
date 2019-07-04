package com.jasper.model;

import com.jasper.model.httpenums.HttpState;
import com.jasper.model.httpenums.Protocol;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeString.trim;

/**
 * Model for a request to be send / used.
 */
public class HttpRequest {

    private HttpState state = HttpState.READ_METHOD;
    private StringBuilder stateBuilder = new StringBuilder();
    private StatusCode statusCode = StatusCode.INTERNAL_SERVER_ERROR; //status code of request.

    //Method variables.
    private RequestType requestMethod = null;    //Method GET / POST

    //URL variables.
    private StateUrl stateUrl = StateUrl.READ_PROTOCOL;
    private Protocol protocol = null;
    private String authority = null; //is host +":" +  port
    private String host = null; //name of website, minus Protocol or port
    private Integer port = 80; //80 is default port.
    private String path = null;
    private String query = null; //everything behind the question mark on GET request
    private String filename = null; //path + query.
    private String ref = null; //Bookmark with # which part of the page it should put as top.

    private Map<String, String> queryGET = new HashMap<>(); //for GET Request
    private Map<String, String> queryPOST = new HashMap<>();

    //Headers.
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder headerName = new StringBuilder();
    private StringBuilder headerValue = new StringBuilder();
    private boolean upgradingConnection = false;
    private String upgradeSecureKeyAnswer = null;

    //HttpVersion number.
    private String httpVersion;

    public void setRequestMethod(RequestType requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RequestType getRequestMethod() {
        return requestMethod;
    }

    public HttpState getState() {
        return state;
    }

    public void setState(HttpState status) {
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

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
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

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String headerName, String headerValue) {
        this.headers.put(removeSemicolon(headerName), trim(removeRN(headerValue)));
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

    public Map<String, String> getQueryGET() {
        return queryGET;
    }

    public void setQueryGET(Map<String, String> queryGET) {
        this.queryGET = queryGET;
    }

    public Map<String, String> getQueryPOST() {
        return queryPOST;
    }

    public void setQueryPOST(Map<String, String> queryPOST) {
        this.queryPOST = queryPOST;
    }

    private String removeRN(String inputString) {
        return inputString.replaceAll("(\r\n|\n)", "");
    }

    private String removeSemicolon(String inputString) {
        //$ means last part of string.
        return inputString.replaceAll(":$", "");
    }

    public boolean isUpgradingConnection() {
        return upgradingConnection;
    }

    public void setUpgradingConnection(boolean upgradingConnection) {
        this.upgradingConnection = upgradingConnection;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    protected String getUpgradeSecureKeyAnswer() {
        return upgradeSecureKeyAnswer;
    }

    public void setUpgradeSecureKeyAnswer(String upgradeSecureKeyAnswer) {
        this.upgradeSecureKeyAnswer = upgradeSecureKeyAnswer;
    }
}
