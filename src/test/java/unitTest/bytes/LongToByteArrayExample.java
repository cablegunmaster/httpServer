package unitTest.bytes;

import java.io.IOException;

public class LongToByteArrayExample {
    public static void main(String[] args) throws IOException {
        long aliveTime = System.currentTimeMillis();
        System.out.println("Time: " + aliveTime);
        byte[] longAsBytes = new byte[8];
        longToByteArray(aliveTime, longAsBytes);
        long retrievedAliveTime = byteArrayToLong(longAsBytes);
        System.out.println("Retrieved Time: " + retrievedAliveTime);        
    }
    
    private static void longToByteArray(long l, byte[] b) {
        b[7] = (byte) (l);
        l >>>= 8;
        b[6] = (byte) (l);
        l >>>= 8;
        b[5] = (byte) (l);
        l >>>= 8;
        b[4] = (byte) (l);
        l >>>= 8;
        b[3] = (byte) (l);
        l >>>= 8;
        b[2] = (byte) (l);
        l >>>= 8;
        b[1] = (byte) (l);
        l >>>= 8;
        b[0] = (byte) (l);
    }
    
    private static long byteArrayToLong(byte[] b){
        return ( ( (long) b[7]) & 0xFF) +
            ( ( ( (long) b[6]) & 0xFF) << 8) +
            ( ( ( (long) b[5]) & 0xFF) << 16) +
            ( ( ( (long) b[4]) & 0xFF) << 24) +
            ( ( ( (long) b[3]) & 0xFF) << 32) +
            ( ( ( (long) b[2]) & 0xFF) << 40) +
            ( ( ( (long) b[1]) & 0xFF) << 48) +
            ( ( ( (long) b[0]) & 0xFF) << 56);
    }
}
