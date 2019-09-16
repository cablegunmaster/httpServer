package com.jasper.model.socket.models.utils;

public class ByteUtil {

    /**
     * @param bitFromRight (0 - 7) is range.
     */
    public static boolean checkBitActivated(int bitFromRight, int value) {
        return ((value >> bitFromRight) & 0x01) == 1;
    }
}
