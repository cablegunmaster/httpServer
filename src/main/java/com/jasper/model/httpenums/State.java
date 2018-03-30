package com.jasper.model.httpenums;

/**
 * States for reading Request.
 */
public enum State {

    READ_METHOD,
    READ_URI,
    READ_HTTP,
    READ_HEADER_NAME,
    READ_HEADER_VALUE,
    READ_BODY,
    DONE,
    ERROR;

    public boolean isReadingMethod(){
        return this == State.READ_METHOD;
    }
    public boolean isReadingURI(){
        return this == State.READ_URI;
    }

    public boolean isReadingHttpVersion(){
        return this == State.READ_HTTP;
    }

    public boolean isReadingHeaderName() {
        return this == State.READ_HEADER_NAME;
    }

    public boolean isReadingHeaderValue() {
        return this == State.READ_HEADER_VALUE;
    }

    public boolean isReadingBody() {
        return this == State.READ_BODY;
    }

    public boolean isDone() {
        return this == State.DONE;
    }

    public boolean isErrorState() {
        return this == State.ERROR;
    }
}
