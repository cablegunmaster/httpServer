package com.jasper.model.response;

import javax.annotation.Nonnull;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SocketResponse {

    /**
     * http://www.herongyang.com/Java/Bit-String-Stored-in-Byte-Array-Test-Program.html
     * 129 (?) 128 betekent einde bericht. dat is FIN, RSV
     * 129 (128 betekent masking bit is gezet) dus 129 - 128  = 1  byte die je verder moet lezen
     * <p>
     * 213 ( byte lezen) is de letter "a" maar dan in cijfer en gemaskerd als iets anders in combinatie met mask kan het de letter a weer worden.
     * <p>
     * (masker combinatie bestaat uit 4  bytes, elke byte is een nummer van 1-255 )
     * 91
     * 187
     * 242
     * 180
     */
    public static byte[] createSocketResponse(@Nonnull String message) {
        int length = message.length();
        byte[] destinationBuffer;
        byte[] startBuffer = new byte[2];
        byte lengthByte = (byte) message.length();
        int OffsetBytes = 2;

        if (length <= 125) {
            startBuffer = new byte[2];
            startBuffer[0] = (byte) 129;
            startBuffer[1] = lengthByte;
        }

        if (message.length() > 125 && message.length() < 65535) {
            startBuffer = new byte[4];
            startBuffer[0] = (byte) 129;
            startBuffer[1] = 126;

            //load up other 2 bytes.
            shortToByteArray(lengthByte, startBuffer);
            OffsetBytes = 4;
        }

        if (message.length() > 65535 && message.length() < 2147483647) {
            startBuffer = new byte[10];
            startBuffer[0] = (byte) 129;
            startBuffer[1] = 127;

            //offset because we have  Startbuffer already
            longToByteArray((message.length()), startBuffer, 2);
            OffsetBytes = 10;
        }

        byte[] endBuffer = message.getBytes(UTF_8);
        destinationBuffer = new byte[endBuffer.length + startBuffer.length];

        System.arraycopy(startBuffer, 0, destinationBuffer, 0, startBuffer.length);
        System.arraycopy(endBuffer, 0, destinationBuffer, OffsetBytes, endBuffer.length);

        return destinationBuffer;
    }

    //unsigned 16 bit integer
    public static void shortToByteArray(long l, byte[] startBuffer) {
        if (l <= 255) {
            startBuffer[3] = (byte) (l); //smallest number goes in the back.
            startBuffer[2] = (byte) 0;
        } else {
            startBuffer[3] = (byte) (l); //smallest number goes in the back.
            l >>>= 8; //get next byte from short.
            startBuffer[2] = (byte) (l); //biggest number upfront?
        }
    }

    //to 8 bytes 64 bit unsigned.
    private static void longToByteArray(long l, byte[] b, Integer offset) {
        if (offset == null) {
            offset = 0;
        }

        b[7 + offset] = (byte) (l);
        l >>>= 8;
        b[6 + offset] = (byte) (l);
        l >>>= 8;
        b[5 + offset] = (byte) (l);
        l >>>= 8;
        b[4 + offset] = (byte) (l);
        l >>>= 8;
        b[3 + offset] = (byte) (l);
        l >>>= 8;
        b[2 + offset] = (byte) (l);
        l >>>= 8;
        b[1 + offset] = (byte) (l);
        l >>>= 8;
        b[offset] = (byte) (l); //byte 0
    }

}
