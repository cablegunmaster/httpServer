package com.jasper.model.httpenums;

public enum SocketMessageState {
    END_MESSAGE,
    LENGTH,
    CONTENT,
    MASK,
    CONTENT_TO_STRING;
}
