package com.jasper.model.http.enums;

/**
 * Viewed on 16/0/2018 https://developer.mozilla.org/nl/docs/Web/HTTP/Status
 */
public enum StatusCode {

    //1xx Informational
    CONTINUE("100", "Continue"),
    SWITCHING_PROTOCOL("101", "Switching Protocol"),
    PROCESSING("102", "Processing"),

    //2xx Success
    OK("200", "OK"),
    CREATED("201", "Created"),
    ACCEPTED("202", "Accepted"),
    NONAUTHORITIVEINFORMATION("203", "Non-Authoritive information"),
    NOCONTENT("204", "No-Content"),
    RESETCONTENT("205", "Reset Content"),
    PARTIALCONTENT("206", "Partial Content"),
    MULTISTATUS("207", "Multi-Status"),
    ALREADYREPORTED("208","Already Reported"),
    IMUSED("226", "IM Used"),

    //3xx Redirection
    MULTIPLECHOICES("300","Multiple Choices"),
    MOVED_PERMANENTLY("301", "Moved Permanently"),
    FOUND("302", "Found"),
    SEE_OTHER("303", "See Other"),
    NOT_MODIFIED("304", "Not Modified"),
    USE_PROXY("305", "Use Proxy"),
    UNUSED("306", "Unused"),
    TEMPORARY_REDIRECT("307", "Temporary Redirect"),
    PERMANENT_REDIRECT("308", "Permanent Redirect (experimental)"),

    //4xx Client Error
    BAD_REQUEST("400", "Bad Request"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Not Found"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed"),
    REQUEST_TIMEOUT("408", "Request Timeout"),
    LENGTH_REQUIRED("411", "Length Required"),
    PAYLOAD_TO_LARGE("413", "Payload Too Large"),
    URI_TOO_LONG("414", "URI Too Long"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    HTTP_VERSION_NOT_SUPPORTED("505", "HTTP Version Not Supported");

    private final String statusCodeNumber;
    private final String description;

    StatusCode(String number, String description) {
        this.statusCodeNumber = number;
        this.description = description;
    }

    public String getStatusCode() {
        return statusCodeNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusCodeNumber() {
        return statusCodeNumber;
    }
}
