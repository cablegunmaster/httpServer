package com.jasper.model.http.enums;

/**
 * States for reading Request.
 */
public enum HttpState {
    READ_METHOD,
    READ_URI,
    READ_HTTP,
    READ_HEADER_NAME,
    READ_HEADER_VALUE,
    READ_BODY,
    DONE,
    ERROR;

    public boolean isReadingMethod() {
        return this == READ_METHOD;
    }

    public boolean isReadingURI() {
        return this == READ_URI;
    }

    public boolean isReadingHttpVersion() {
        return this == READ_HTTP;
    }

    public boolean isReadingHeaderName() {
        return this == READ_HEADER_NAME;
    }

    public boolean isReadingHeaderValue() {
        return this == READ_HEADER_VALUE;
    }

    public boolean isReadingBody() {
        return this == READ_BODY;
    }

    public boolean isDone() {
        return this == DONE;
    }

    public boolean isErrorState() {
        return this == ERROR;
    }
}
