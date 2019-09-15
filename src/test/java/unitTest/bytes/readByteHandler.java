package unitTest.bytes;

import com.jasper.model.socket.models.SocketMessageParser;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class readByteHandler {

    //aaaaa 5 char
    List<Integer> oneByteList = Arrays.asList(
            129,
            133,

            8,
            25,
            234,
            118,
            105,

            120, 139, 23, 105);
    List<Integer> secondByteList = Arrays.asList(
            129,
            129,
            231,
            146,
            151,
            92,
            134);

    //abDDDE
    List<Integer> thirdByteList = Arrays.asList(
            129,
            134,
            249,
            211,
            204,
            40,
            152,
            177,
            136,
            108,
            189,
            150);

    List<Integer> fourthByteList = Arrays.asList(
            129,
            131,
            219,
            106,
            43,
            189,
            186,
            40,
            104);


    /**
     * 129
     * 131
     * <p>
     * 219 A
     * 106 B
     * 43  C
     * <p>
     * 189 /m1
     * 186 /m2
     * 40  /m3
     * 104 /m4
     */

    @Test
    public void testHandler() {
        SocketMessageParser smParser = new SocketMessageParser();

        for (int i : oneByteList) {
            smParser.parseMessage(i);
        }
    }

    //Stacks principe?
    @Test
    public void maskHandleraa() {
        byte[] decoded = new byte[2];
        byte[] encoded = new byte[]{(byte) 161, (byte) 162};
        byte[] key = {(byte) 192, (byte) 195, (byte) 226, (byte) 166};

        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
        }
        System.out.println(new String(decoded, StandardCharsets.UTF_8));
    }

    @Test
    public void maskHandleraBC() {

        //Stack?
        /**
         * 129
         * 131
         *
         * 219 a
         * 106 B
         * 43  C
         *
         * //Mask  from M4 -> M1 to use instead of M1->M4.???WRONG
         * 189 /m1
         * 186 /m2
         * 40  /m3
         * 104 /m4
         */

        byte[] decoded = new byte[3];
        byte[] encoded = new byte[]{(byte) 43, (byte) 106, (byte) 219};
        byte[] key = {(byte) 104, (byte) 40, (byte) 186, (byte) 189};

        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
        }

        System.out.println(
                new StringBuilder(new String(decoded, StandardCharsets.UTF_8))
                        .reverse()
                        .toString()
        );
    }

    //abcdef
    @Test
    public void maskHandler() {
        byte[] decoded = new byte[6];
        byte[] encoded = new byte[]{(byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135};
        byte[] key = {(byte) 167, (byte) 225, (byte) 225, (byte) 210};

        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
        }
        System.out.println(new String(decoded, StandardCharsets.UTF_8));
    }
}
