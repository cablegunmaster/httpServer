package com.jasper.model.request.uriparser;

import com.jasper.model.httpenums.SocketMessageState;
import com.jasper.model.socketEnum.OpCode.OpCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.jasper.model.httpenums.SocketMessageState.*;

public class SocketMessageParser {

    private final static int MASK_SIZE = 4;

    private boolean isFinMessage = false;
    private boolean isMaskSet = false;
    private OpCode opCode;

    private boolean messageReady = false;
    private StringBuffer message = new StringBuffer();
    private Long messageLength = 0L;

    private List<Integer> lengthList = new ArrayList<>();
    private List<Integer> content = new ArrayList<>();
    private List<Integer> maskList = new ArrayList<>();
    private SocketMessageState state = END_MESSAGE;

    /**
     * Reset when message is read.
     */
    public void reset() {
        state = END_MESSAGE;

        isFinMessage = false;
        isMaskSet = false;
        messageReady = false;
        clear(message);
        messageLength = 0L;

        lengthList.clear();
        content.clear();
        maskList.clear();
    }

    /**
     * Message parsed to string.
     * ONLY IMPLEMENTED STEP 1 (if size is smaller as 126)
     *
     * @param input is based on the
     */
    public void parseMessage(int input) {
        switch (state) {
            case END_MESSAGE:
                setFinBit(input);
                //RSV not needed for some kind of 'negotiation'?
                setOpcode(input);

                //go to length
                state = LENGTH;
                break;
            case LENGTH:
                if (checkBitActivated(7, input)) {
                    isMaskSet = true;
                    messageLength = (input - 128L); //shift to remove last bit
                } else {
                    messageLength = (long) input;
                }

                //Payload length:  7 bits, 7+16 bits, or 7+64 bits
                if (messageLength <= 125) {
                    state = MASK;
                }

                if (messageLength == 126) {
                    //read next 2 bytes as 16 bit unsigned integer.
                    //unsigned can go higher has no negative numbers.
                    state = LENGTH_SIXTEEN_BIT;
                }

                if (messageLength == 127) {
                    state = LENGTH_SIXTY_FOUR_BIT;
                    //  the following 8 bytes interpreted as a 64-bit unsigned integer (the
                    //   most significant bit MUST be 0
                }

                break;

            case LENGTH_SIXTEEN_BIT:
                lengthList.add(input);

                if (lengthList.size() == 2) {
                    short int16 = (short) (((lengthList.get(0) & 0xFF) << 8) | (lengthList.get(1) & 0xFF));
                    messageLength = (long) int16;
                    state = MASK;
                }
                break;
            case LENGTH_SIXTY_FOUR_BIT:
                lengthList.add(input);
                if (lengthList.size() == 8) {

                    long result = 0L;
                    int j = 0;

                    for (int i = 8; i > 0; i--) {
                        result = result + ((lengthList.get(j) & 0xFF) << (8 * i));
                        j++;
                    }

                    messageLength = (long) result;
                    state = MASK;
                }
                break;
            case MASK:
                if (maskList.size() < MASK_SIZE) {
                    maskList.add(input);
                    if (maskList.size() == MASK_SIZE) {
                        state = CONTENT;
                    }
                }
                break;
            case CONTENT:
                if (content.size() < messageLength) {
                    content.add(input);
                    if (content.size() == messageLength) {
                        state = END_MESSAGE;
                        decodeMessage();
                        messageReady = true;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void setFinBit(int inputFirstByte) {
        isFinMessage = checkBitActivated(7, inputFirstByte);
    }

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

    private void decodeMessage() {
        byte[] decoded = new byte[content.size()];

        for (int i = 0; i < content.size(); i++) {
            decoded[i] = (byte) (content.get(i) ^ (maskList.get(i % 4)));
        }
        message.append(new String(decoded, StandardCharsets.UTF_8));
    }

    public boolean getMessageReady() {
        return messageReady;
    }

    public String getMessage() {
        return message.toString();
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public void clear(StringBuffer s) {
        s.setLength(0);
    }
}
