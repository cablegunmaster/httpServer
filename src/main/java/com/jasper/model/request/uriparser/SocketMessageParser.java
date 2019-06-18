package com.jasper.model.request.uriparser;

import com.jasper.model.httpenums.SocketMessageState;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.jasper.model.httpenums.SocketMessageState.CONTENT;
import static com.jasper.model.httpenums.SocketMessageState.END_MESSAGE;
import static com.jasper.model.httpenums.SocketMessageState.LENGTH;
import static com.jasper.model.httpenums.SocketMessageState.MASK;

public class SocketMessageParser {

    private final static int MASK_SIZE = 4;
    private Boolean messageReady = false;
    private String message;
    private int messageLength = 0;

    private List<Integer> content = new ArrayList<>();
    private List<Integer> maskList = new ArrayList<>();
    private SocketMessageState state = END_MESSAGE;

    private void reset() {
        state = END_MESSAGE;

        message = null;
        messageLength = 0;
        messageReady = false;

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
                //Check if its end of message
                if (checkBitActivated(7, input)) {
                    reset();
                }
                state = LENGTH;
                break;
            case LENGTH:
                if (checkBitActivated(7, input)) {
                    messageLength = (input - 128); //shift to remove last bit
                    state = MASK;
                } else {
                    messageLength = input;
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
                reset();
                break;
        }
    }

    /**
     * @param bitFromRight (0 - 7) is range.
     */
    private Boolean checkBitActivated(int bitFromRight, int value) {
        return ((value >> bitFromRight) & 0x01) == 1;
    }

    /**
     * Get unsigned byte from Integer.
     * https://android.jlelse.eu/java-when-to-use-n-8-0xff-and-when-to-use-byte-n-8-2efd82ae7dd7
     */

    private void decodeMessage() {
        byte[] decoded = new byte[content.size()];

        for (int i = 0; i < content.size(); i++) {
            decoded[i] = (byte) (content.get(i) ^ (maskList.get(i % 4)));
        }
        message = new String(decoded, StandardCharsets.UTF_8);
        System.out.println(message);
    }


    public boolean getMessageReady() {
        return messageReady;
    }

    public String getMessage() {
        return message;
    }
}
