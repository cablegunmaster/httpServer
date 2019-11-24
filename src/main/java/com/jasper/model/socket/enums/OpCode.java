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

    //Control Frames.
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

    //Hex to int.
    public int getIntOpCode() {
        return Integer.parseInt(this.value, 16);
    }

    public boolean isPing() {
        return this.name().equals("PING");
    }

    public boolean isText() {
        return this.name().equals("TEXT");
    }

    //TODO implement continuation?
    public boolean isContinuation() {
        return this.name().equals("CONTINUATION");
    }

    public boolean isControlFrame() {
        return this.name().equals("CLOSE") || this.name().equals("PING") || this.name().equals("PONG");
    }

    public boolean isClose() {
        return this.name().equals("CLOSE");
    }
}
