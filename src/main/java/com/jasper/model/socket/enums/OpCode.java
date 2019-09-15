package com.jasper.model.socket.enums;

import java.util.Arrays;

/**
 * %x0 denotes a continuation frame
 * %x1 denotes a text frame
 * %x2 denotes a binary frame
 * %x3-7 are reserved for further non-control frames
 * %x8 denotes a connection close
 * %x9 denotes a ping
 * %xA denotes a pong
 * %xB-F are reserved for further control frames
 */
public enum OpCode {
    CONTINUATION("0"),
    TEXT("1"),
    BINARY("2"),

    CLOSE("8"),
    PING("9"),
    PONG("A"); //0xA

    private final String value;

    OpCode(String value) {
        this.value = value;
    }

    public static OpCode findByValue(final String inputString) {
        return Arrays.stream(values())
                .filter(value -> value.value.equals(inputString))
                .findFirst()
                .orElse(null);
    }

    public boolean isText(){
        return this.name().equals("TEXT");
    }

    public boolean isContinuation(){
        return this.name().equals("CONTINUATION");
    }
}
