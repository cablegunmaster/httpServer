package com.jasper.model.http.enums;

/**
 * Viewed on 16/0/2018 https://developer.mozilla.org/nl/docs/Web/HTTP/Status
 * https://www.restapitutorial.com/httpstatuscodes.html
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
    NON_AUTHORITIVE_INFORMATION("203", "Non-Authoritive information"),
    NOCONTENT("204", "No-Content"),
    RESET_CONTENT("205", "Reset Content"),
    PARTIAL_CONTENT("206", "Partial Content"),
    MULTI_STATUS("207", "Multi-Status"),
    ALREADY_REPORTED("208", "Already Reported"),
    IM_USED("226", "IM Used"),

    //3xx Redirection of page.
    MULTIPLECHOICES("300", "Multiple Choices"),
    MOVED_PERMANENTLY("301", "Moved Permanently"),
    FOUND("302", "Found"),
    SEE_OTHER("303", "See Other"),
    NOT_MODIFIED("304", "Not Modified"),
    USE_PROXY("305", "Use Proxy"),
    UNUSED("306", "Unused"),
    TEMPORARY_REDIRECT("307", "Temporary Redirect"),
    PERMANENT_REDIRECT("308", "Permanent Redirect (experimental)"),

    //4xx Client made a misstake Error
    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),
    PAYMENT_REQUIRED("402", "Payment Required"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Not Found"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed"),
    NOT_ACCEPTABLE("406", "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED("407", "Proxy Authentication Required"),
    REQUEST_TIMEOUT("408", "Request Timeout"),
    CONFLICT("409", "Conflict"),
    GONE("410", "Gone"),
    LENGTH_REQUIRED("411", "Length Required"),
    PRECONDITION_FAILED("412", "Precondition failed"),
    PAYLOAD_TO_LARGE("413", "Payload Too Large"),
    URI_TOO_LONG("414", "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE("415", "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE("416", "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED("417", "Expection failed"),
    IM_A_TEAPOT("418", "I'm a teapot"),
    ENHANCE_YOUR_CALM("420", "Enhance Your Calm (Twitter)"),
    UNPROCESSABLE_ENTITY("422", "Unprocessable Entity (WebDAV)"),
    LOCKED("423", "Locked WebDAV"),
    FAILED_DEPENDANCY("424", "Failed Dependency (WebDAV)"),
    RESERVED_FOR_WEBDAV("425", "Reserved for WebDAV"),
    UPGRADE_REQUIRED("426", "Upgrade Required"),
    PRECONDITION_REQUIRED("428", "Precondition Required"),
    TOO_MANY_REQUESTS("429", "Upgrade Required"),
    REQUEST_HEADER_FIELDS_TOO_LARGE("431", "Request header fields too large"),
    NO_RESPONSE("444", "No Response (Nginx)"),
    RETRY_WITH_MICROSOFT("449", "Retry With Microsoft"),
    BLOCKED_BY_WINDOWS_PARENTAL("450", "Blocked by Windows Parental Controls (Microsoft)"),
    UNAVAILABLE_FOR_LEGAL_REASONS("451", "Unavailable for legal reasons"),
    CLIENT_CLOSED_REQUEST("499", "Client Closed Request (Nginx)"),

    //5xx Server made booboo
    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    NOT_IMPLEMENTED("501", "Not Implented"),
    BAD_GATEWAY("502", "Bad Gateway"),
    SERVICE_UNAVAILABLE("503", "Service Unavailable"),
    GATEWAY_TIMEOUT("504", "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED("505", "HTTP Version Not Supported"),
    VARIANT_ALS_NEGOTIATES("506", "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE("507", "Insufficient Storage (WebDAV)"),
    LOOP_DETECTED("508", "Loop detected (WebDAV)"),
    BANDWITH_LIMIT_EXCEEDED("509", "Bandwidth Limit Exceeded (Apache)"),
    NOT_EXTENDED("510", "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED("511", "Network Authentication Required"),
    NETWORK_READ_TIMEOUT_ERROR("598", "Network read timeout error"),
    NETWORK_CONNECT_TIMEOUT_ERROR("599", "Network connect timeout error");

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
