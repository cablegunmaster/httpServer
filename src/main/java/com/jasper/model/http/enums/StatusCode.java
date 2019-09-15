package com.jasper.model.http.enums;

/**
 * Viewed on 16/0/2018 https://developer.mozilla.org/nl/docs/Web/HTTP/Status
 */
public enum StatusCode {
    SWITCHING_PROTOCOL("101", "Switching Protocol"),
    OK("200", "OK"),
    ACCEPTED("202", "Accepted"),
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
