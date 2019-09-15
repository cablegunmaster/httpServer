package com.jasper.model.socket.entity;

import com.jasper.model.http.enums.SocketMessageState;
import com.jasper.model.socket.enums.OpCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.jasper.model.http.enums.SocketMessageState.END_FRAME;

public class Frame {

    private boolean isFinMessage = false;
    private boolean isMaskSet = false;
    private OpCode opCode;
    private Long messageLength = 0L;
    private SocketMessageState state = END_FRAME;

    private StringBuffer message = new StringBuffer();
    private List<Integer> lengthList = new ArrayList<>();
    private List<Integer> content = new ArrayList<>();
    private List<Integer> maskList = new ArrayList<>();

    /**
     * @param bitFromRight (0 - 7) is range.
     */
    public boolean checkBitActivated(int bitFromRight, int value) {
        return ((value >> bitFromRight) & 0x01) == 1;
    }

    //4 bit to hexadecimal.
    public void setOpcode(int value) {
        StringBuilder bitRange = new StringBuilder();

        for (int i = 3; i >= 0; i--) {
            if (checkBitActivated(i, value)) {
                bitRange.append("1");
            } else {
                bitRange.append("0");
            }
        }

        opCode = OpCode.findByValue(
                Long.toHexString(
                        Long.parseLong(bitRange.toString(), 2))
                        .toUpperCase());
    }

        /*
    Payload length:  7 bits, 7+16 bits, or 7+64 bits

    The length of the payload data, in bytes: if 0-125, that is the
    payload length.  If 126, the following 2 bytes interpreted as a 16
    bit unsigned integer are the payload length.  If 127, the
    following 8 bytes interpreted as a 64-bit unsigned integer
    (the most significant bit MUST be 0) are the payload length.  Multibyte
    length quantities are expressed in network byte order.  The
    payload length is the length of the extension data + the length of
    the application data.  The length of the extension data may be
    zero, in which case the payload length is the length of the
    application data.
    */

    /**
     * Get unsigned byte from Integer.
     * https://android.jlelse.eu/java-when-to-use-n-8-0xff-and-when-to-use-byte-n-8-2efd82ae7dd7
     */
    public void decodeMessage() {
        byte[] decoded = new byte[content.size()];

        for (int i = 0; i < content.size(); i++) {
            decoded[i] = (byte) (content.get(i) ^ (maskList.get(i % 4)));
        }
        message.append(new String(decoded, StandardCharsets.UTF_8));
    }

    public void setFinBit(int inputFirstByte) {
        isFinMessage = checkBitActivated(7, inputFirstByte);
    }

    public SocketMessageState getState() {
        return state;
    }

    public Long getMessageLength() {
        return messageLength;
    }

    public void setFinMessage(boolean finMessage) {
        isFinMessage = finMessage;
    }

    public void setMaskSet(boolean b) {
        isMaskSet = b;
    }

    public void setOpCode(OpCode opCode) {
        this.opCode = opCode;
    }

    public void setMessageLength(Long messageLength) {
        this.messageLength = messageLength;
    }

    public void setState(SocketMessageState state) {
        this.state = state;
    }

    public boolean isFinMessage() {
        return isFinMessage;
    }

    public boolean isMaskSet() {
        return isMaskSet;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public StringBuffer getMessage() {
        return message;
    }

    public List<Integer> getLengthList() {
        return lengthList;
    }

    public void setLengthList(List<Integer> lengthList) {
        this.lengthList = lengthList;
    }

    public void setMessage(StringBuffer message) {
        this.message = message;
    }

    public List<Integer> getContent() {
        return content;
    }

    public void setContent(List<Integer> content) {
        this.content = content;
    }

    public List<Integer> getMaskList() {
        return maskList;
    }

    public void setMaskList(List<Integer> maskList) {
        this.maskList = maskList;
    }
}
