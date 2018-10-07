package com.jasper.model.request.uriparser;

import com.jasper.model.httpenums.SocketMessageState;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.jasper.model.httpenums.SocketMessageState.*;

public class SocketMessageParser {

    private final static int MASK_SIZE = 4;
    private Boolean finalMessage = false;
    private Boolean mask = false;
    private String message = null;
    private int messageLength = 0;

    private List<Integer> content = new ArrayList<>();
    private List<Integer> maskList = new ArrayList<>();
    private SocketMessageState state = ENDMESSAGE;

    /**
     * Message parsed to string.
     * ONLY IMPLEMENTED STEP 1 (if size is smaller as 126)
     *
     * @param input is based on the
     */
    public void parseMessage(int input) throws UnsupportedEncodingException {
        switch (state) {
            case ENDMESSAGE:
                //Check if its end of message
                if (checkBitActivated(7, input)) {
                    finalMessage = true;
                }
                state = LENGTH;
                break;
            case LENGTH:
                if (checkBitActivated(7, input)) {
                    mask = true;
                    messageLength = (input - 128); //shift to remove last bit
                    state = CONTENT;
                } else {
                    messageLength = input;
                }
                break;
            case CONTENT:
                if (content.size() < messageLength) {
                    content.add(input);
                    if (content.size() == messageLength) {
                        state = MASK;
                    }
                }
                break;
            case MASK:
                if (maskList.size() < MASK_SIZE) {
                    maskList.add(input);
                    if (maskList.size() == MASK_SIZE) {
                        state = CONTENT_TO_STRING;
                        decodeMessage();
                    }
                }
                //Add to
                break;
            case CONTENT_TO_STRING:
                decodeMessage();
                break;
        }
    }

    /**
     * @param bitFromRight (0 - 7) is range.
     * @return
     */
    public Boolean checkBitActivated(int bitFromRight, int value) {
        return ((value >> bitFromRight) & 0x01) == 1;
    }

    /**
     * Get unsigned byte from Integer.
     * https://android.jlelse.eu/java-when-to-use-n-8-0xff-and-when-to-use-byte-n-8-2efd82ae7dd7
     */

    public void decodeMessage() throws UnsupportedEncodingException {
        byte[] decoded = new byte[content.size()];

        for (int i = 0; i < content.size(); i++) {
            decoded[i] = (byte) (content.get(i) ^ (maskList.get(i % 4)));
        }
        System.out.println(new String(decoded, "UTF-8"));
    }
}
