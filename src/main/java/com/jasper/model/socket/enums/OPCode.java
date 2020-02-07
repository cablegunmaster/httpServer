package com.jasper.model.socket.enums;

import java.util.Arrays;
import java.util.Objects;

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
public enum OPCode {
    CONTINUATION("0"),
    TEXT("1"),
    BINARY("2"),

    //Control Frames.
    CLOSE("8"),
    PING("9"),
    PONG("A"); //0xA

    private final String value;

    OPCode(String value) {
        this.value = value;
    }

    public static OPCode findByValue(final String inputString) {
        return Arrays.stream(values())
                .filter(value -> value.value.equals(inputString))
                .findFirst()
                .orElse(null);
    }

    //Hex to int.
    public int getIntOpCode() {
        return Integer.parseInt(this.value, 16);
    }

    public boolean isTypeOfControlFrame() {
        return Objects.equals(this, CLOSE) ||
                Objects.equals(this, PING) ||
                Objects.equals(this, PONG);
    }
    public boolean isPing() {
        return Objects.equals(this, PING);
    }

    public boolean isText() {
        return Objects.equals(this, TEXT);
    }

    public boolean isContinuation() {
        return Objects.equals(this, CONTINUATION);
    }

    public boolean isClose() {
        return Objects.equals(this, CLOSE);
    }
}
