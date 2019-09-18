package com.jasper.model.socket.models;

import com.jasper.model.socket.enums.OpCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SocketResponse {

    private final static Logger LOG = LoggerFactory.getLogger(SocketResponse.class);

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
    public static byte[] createSocketResponse(@Nonnull String message, OpCode opCode) {
        int length = message.length();
        byte[] destinationBuffer;
        byte[] startBuffer = new byte[2];
        byte lengthByte = (byte) message.length();
        int OffsetBytes = 0;

        if (length <= 125) {
            startBuffer = new byte[2];
            startBuffer[0] = (byte) (128 + opCode.getIntOpCode());
            startBuffer[1] = lengthByte;
            OffsetBytes = 2;
        }

        if (message.length() > 125 && message.length() < 65535) {
            startBuffer = new byte[4];
            startBuffer[0] = (byte) (128 + opCode.getIntOpCode());
            startBuffer[1] = 126;
            fill2ByteInteger(message.length(), startBuffer); //load up 2 bytes.
            OffsetBytes = 4;
        }

        if (message.length() > 65535 && message.length() < 2147483647) {
            startBuffer = new byte[10];
            startBuffer[0] = (byte) (128 + opCode.getIntOpCode());
            startBuffer[1] = 127;
            fill8ByteArrayFromInteger(message.length(), startBuffer);
            OffsetBytes = 10;
        }

        byte[] endBuffer = message.getBytes(UTF_8);
        destinationBuffer = new byte[endBuffer.length + startBuffer.length];

        System.arraycopy(startBuffer, 0, destinationBuffer, 0, startBuffer.length);
        System.arraycopy(endBuffer, 0, destinationBuffer, OffsetBytes, endBuffer.length);

        return destinationBuffer;
    }

    //unsigned 16 bit integer
    public static void fill2ByteInteger(long l, byte[] startBuffer) {
        startBuffer[3] = (byte) (l); //smallest number goes in the back.
        l >>>= 8;
        startBuffer[2] = (byte) (l & 0xff);
    }

    //to 8 bytes 64 bit unsigned.
    private static void fill8ByteArrayFromInteger(@Nonnull Integer l, byte[] b) {
        //make a 8 byte from the length back again.
        if (b.length == 10) {
            int preBytes = 2;
            for (int i = 7; i >= 0; i--) {
                b[i + preBytes] = (byte) (l.longValue());
                l >>>= 8;
            }
        } else {
            LOG.info("Byte array in Socket response should be 10 long. is now: {}", b.length);
        }
    }

}
