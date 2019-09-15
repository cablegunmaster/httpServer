package com.jasper.model.http.enums;

public enum PostState {
    READ_POST_NAME,
    READ_POST_VALUE;

    public boolean isPostName() {
        return this == READ_POST_NAME;
    }

    public boolean isPostValue() {
        return this == READ_POST_VALUE;
    }
}
