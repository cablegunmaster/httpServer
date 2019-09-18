package com.jasper.model.socket.models;

import com.jasper.model.http.enums.SocketMessageState;
import com.jasper.model.socket.models.entity.Frame;

import java.util.ArrayList;
import java.util.List;

import static com.jasper.model.http.enums.SocketMessageState.*;
import static com.jasper.model.socket.models.utils.ByteUtil.checkBitActivated;

public class SocketMessageParser {

    private final static int MASK_SIZE = 4;

    private Frame frame = new Frame();
    private SocketMessageState state = END_FRAME;
    private ArrayList<Integer> byteLength = new ArrayList<>();

    /**
     * Reset when message is read.
     */
    public void reset() {
        frame = new Frame();
        state = END_FRAME;
        byteLength.clear();
    }

    /**
     * Message parsed to string.
     * ONLY IMPLEMENTED STEP 1 (if size is smaller as 126)
     *
     * @param input is based on the
     */
    public void parseMessage(int input) {
        switch (getState()) {
            case END_FRAME:
                frame.setFinBit(input);
                frame.setOpCode(input);
                setState(LENGTH);
                break;
            case LENGTH:
                if (checkBitActivated(7, input)) {
                    frame.setMasked(true);
                    frame.setPayload_len(input - 128L); //shift to remove last bit
                } else {
                    frame.setPayload_len((long) input);
                }

                //Payload length:  7 bits, 7+16 bits, or 7+64 bits
                if (frame.getPayload_len() <= 125) {
                    setState(MASK);
                }

                if (frame.getPayload_len() == 126) {
                    //read next 2 bytes as 16 bit unsigned integer.
                    //unsigned can go higher has no negative numbers.
                    setState(LENGTH_SIXTEEN_BIT);
                }

                if (frame.getPayload_len() == 127) {
                    setState(LENGTH_SIXTY_FOUR_BIT);
                    //  the following 8 bytes interpreted as a 64-bit unsigned integer (the
                    //   most significant bit MUST be 0
                }

                break;

            case LENGTH_SIXTEEN_BIT:
                byteLength.add(input);
                if (byteLength.size() == 2) {
                    frame.setPayload_len((long) (((byteLength.get(0) & 0xFF) << 8) | (byteLength.get(1) & 0xFF)));
                    setState(MASK);
                }
                break;
            case LENGTH_SIXTY_FOUR_BIT:
                byteLength.add(input);
                if (byteLength.size() == 8) {
                    long lengthList = 0L;
                    int j = 0;
                    for (int i = 7; i >= 0; i--) {
                        lengthList = lengthList + ((byteLength.get(j) & 0xFF) << (8 * i));
                        j++;
                    }

                    frame.setPayload_len(lengthList);
                    setState(MASK);
                }
                break;
            case MASK:
                if (frame.getMaskList().size() < MASK_SIZE) {
                    List<Integer> maskList = frame.getMaskList();
                    maskList.add(input);
                    if (maskList.size() == MASK_SIZE) {
                        setState(CONTENT);
                    }
                }
                break;
            case CONTENT:
                if (frame.getPayload().size() < frame.getPayload_len()) {
                    List<Integer> content = frame.getPayload();
                    content.add(input);
                    if (content.size() == frame.getPayload_len()) {
                        setState(END_FRAME);
                    }
                }
                break;
            default:
                break;
        }
    }

    public SocketMessageState getState() {
        return state;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setState(SocketMessageState state) {
        this.state = state;
    }

}
