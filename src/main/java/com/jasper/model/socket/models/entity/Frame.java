package com.jasper.model.socket.models.entity;

import com.jasper.model.http.enums.SocketMessageState;
import com.jasper.model.socket.enums.OpCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.jasper.model.http.enums.SocketMessageState.END_FRAME;
import static com.jasper.model.socket.models.utils.ByteUtil.checkBitActivated;

public class Frame {

    //byte 1.
    private boolean isFinMessage = false;
    private boolean rsv1 = false;
    private boolean rsv2 = false;
    private boolean rsv3 = false;
    private OpCode opCode;

    private boolean isMasked = false;
    private Long payload_len = 0L;

    private List<Integer> maskList = new ArrayList<>(); //max 4.
    private List<Integer> payload = new ArrayList<>(); //content.
    private StringBuffer decodedMessage = new StringBuffer();

    //Finmessage
    public void setFinBit(int inputFirstByte) {
        isFinMessage = checkBitActivated(7, inputFirstByte);
    }

    public boolean isFinMessage() {
        return isFinMessage;
    }

    public boolean isRsv1() {
        return rsv1;
    }

    public void setRsv1(boolean rsv1) {
        this.rsv1 = rsv1;
    }

    public boolean isRsv2() {
        return rsv2;
    }

    public void setRsv2(boolean rsv2) {
        this.rsv2 = rsv2;
    }

    public boolean isRsv3() {
        return rsv3;
    }

    public void setRsv3(boolean rsv3) {
        this.rsv3 = rsv3;
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

    public OpCode getOpCode() {
        return opCode;
    }

    public void setMasked(boolean b) {
        isMasked = b;
    }

    public boolean isMasked() {
        return isMasked;
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
    private void decodeMessage() {
        byte[] decoded = new byte[payload.size()];

        for (int i = 0; i < payload.size(); i++) {
            decoded[i] = (byte) (payload.get(i) ^ (maskList.get(i % 4)));
        }
        decodedMessage.append(new String(decoded, StandardCharsets.UTF_8));
    }

    public StringBuffer getDecodedMessage() {
        if (isFinMessage()) {
            decodeMessage();
        }
        return decodedMessage;
    }

    public Long getPayload_len() {
        return payload_len;
    }

    public void setPayload_len(Long payload_len) {
        this.payload_len = payload_len;
    }

    public List<Integer> getPayload() {
        return payload;
    }

    public List<Integer> getMaskList() {
        return maskList;
    }
}
