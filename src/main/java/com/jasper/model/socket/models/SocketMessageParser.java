package com.jasper.model.socket.models;

import com.jasper.model.socket.entity.Frame;

import java.util.List;

import static com.jasper.model.http.enums.SocketMessageState.*;

public class SocketMessageParser {

    private final static int MASK_SIZE = 4;
    private boolean isFrameReady = false;
    private Frame frame;

    /**
     * Reset when message is read.
     */
    public void reset() {
        isFrameReady = false;
        frame = new Frame();
    }

    /**
     * Message parsed to string.
     * ONLY IMPLEMENTED STEP 1 (if size is smaller as 126)
     *
     * @param input is based on the
     */
    public void parseMessage(int input) {
        switch (frame.getState()) {
            case END_FRAME:
                frame.setFinBit(input);
                frame.setOpcode(input);
                frame.setState(LENGTH);
                break;
            case LENGTH:
                if (frame.checkBitActivated(7, input)) {
                    frame.setMaskSet(true);
                    frame.setMessageLength(input - 128L); //shift to remove last bit
                } else {
                    frame.setMessageLength((long) input);
                }

                //Payload length:  7 bits, 7+16 bits, or 7+64 bits
                if (frame.getMessageLength() <= 125) {
                    frame.setState(MASK);
                }

                if (frame.getMessageLength() == 126) {
                    //read next 2 bytes as 16 bit unsigned integer.
                    //unsigned can go higher has no negative numbers.
                    frame.setState(LENGTH_SIXTEEN_BIT);
                }

                if (frame.getMessageLength() == 127) {
                    frame.setState(LENGTH_SIXTY_FOUR_BIT);
                    //  the following 8 bytes interpreted as a 64-bit unsigned integer (the
                    //   most significant bit MUST be 0
                }

                break;

            case LENGTH_SIXTEEN_BIT:
                List<Integer> byteLength = frame.getLengthList();
                byteLength.add(input);
                if (byteLength.size() == 2) {
                    frame.setMessageLength((long) (((byteLength.get(0) & 0xFF) << 8) | (byteLength.get(1) & 0xFF)));
                    frame.setState(MASK);
                }
                break;
            case LENGTH_SIXTY_FOUR_BIT:
                List<Integer> bigByteLength = frame.getLengthList();
                bigByteLength.add(input);
                if (bigByteLength.size() == 8) {
                    long lengthList = 0L;
                    int j = 0;

                    for (int i = 8; i > 0; i--) {
                        lengthList = lengthList + (bigByteLength.get(j) & 0xFF) << (8 * i);
                        j++;
                    }

                    frame.setMessageLength(lengthList);
                    frame.setState(MASK);
                }
                break;
            case MASK:
                if (frame.getMaskList().size() < MASK_SIZE) {
                    List<Integer> maskList = frame.getMaskList();
                    maskList.add(input);
                    if (maskList.size() == MASK_SIZE) {
                        frame.setState(CONTENT);
                    }
                }
                break;
            case CONTENT:
                if (frame.getContent().size() < frame.getMessageLength()) {
                    List<Integer> content = frame.getContent();
                    content.add(input);
                    if (content.size() == frame.getMessageLength()) {
                        frame.setState(END_FRAME);
                        frame.decodeMessage();
                        setFrameReady(true);
                    }
                }
                break;
            default:
                break;
        }
    }

    public boolean isFrameReady() {
        return isFrameReady;
    }

    public void setFrameReady(boolean frameReady) {
        isFrameReady = frameReady;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
