package com.jasper.model.request.requestenums;

public enum StatusCode {
    OK("200"),
    ACCEPTED("202"),
    BAD_REQUEST("400"),
    FORBIDDEN("403"),
    NOT_FOUND("404"),
    METHOD_NOT_ALLOWED("405"),
    REQUEST_TIMEOUT("408"),
    PAYLOAD_TO_LARGE("413"),
    INTERNAL_SERVER_ERROR("500");

    private final String statusCodeNumber;

    StatusCode(String number) {
        this.statusCodeNumber = number;
    }

    public String getStatusCode() {
        return statusCodeNumber;
    }
}
