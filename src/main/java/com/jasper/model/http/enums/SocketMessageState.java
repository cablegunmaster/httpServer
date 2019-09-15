package com.jasper.model.http.enums;

public enum SocketMessageState {
    END_FRAME,
    LENGTH,
    CONTENT,
    MASK,
    CONTENT_TO_STRING,
    LENGTH_SIXTEEN_BIT,
    LENGTH_SIXTY_FOUR_BIT;
}
