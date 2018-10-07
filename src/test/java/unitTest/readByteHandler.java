package unitTest;

import com.jasper.model.request.uriparser.SocketMessageParser;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class readByteHandler {

    List<Integer> oneByteList = Arrays.asList(129,133,8,25,234,118,105,120,139,23,105);
    List<Integer> secondByteList = Arrays.asList(129,131,219,106,43,189,186,40,104);

    /**
     * 129
     * 131
     *
     * 219 A
     * 106 B
     * 43  C
     *
     * 189 /m1
     * 186 /m2
     * 40  /m3
     * 104 /m4
     */

    @Test
    public void testHandler() throws UnsupportedEncodingException {

        //      "aaaaa"
        //        129
        //        133
        //        8
        //        25
        //        234
        //        118
        //        105
        //        120
        //        139
        //        23
        //        105


        //
//        129
//        133

//        69
//        183
//        152
//        197
//        36

//        214
//        249
//        164
//        36


        SocketMessageParser smParser = new SocketMessageParser();

        for(int i :secondByteList){
            smParser.parseMessage(i);
        }
        smParser.decodeMessage();
    }

//129
//        130

//        162
//        161

//        166
//        226
//        195
//        192

    //Stacks principe?
    @Test
    public void maskHandleraa() throws UnsupportedEncodingException {
        byte[] decoded = new byte[2];
        byte[] encoded = new byte[] {(byte) 161,(byte)  162};
        byte[] key = {(byte) 192, (byte) 195, (byte) 226,(byte)  166};

        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte)(encoded[i] ^ key[i & 0x3]);
        }
        System.out.println(new String(decoded, "UTF-8"));
    }

    @Test
    public void maskHandleraBC() throws UnsupportedEncodingException {

        //Stack?
        /**
         * 129
         * 131
         *
         * 219 A
         * 106 B
         * 43  C
         *
         * 189 /m1
         * 186 /m2
         * 40  /m3
         * 104 /m4
         */

        byte[] decoded = new byte[3];
        byte[] encoded = new byte[] {(byte) 43,(byte) 106,(byte)  219};
        byte[] key = {(byte) 104, (byte) 40, (byte) 186,(byte)  189};

        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte)(encoded[i] ^ key[i & 0x3]);
        }

        System.out.println(new String(decoded, "UTF-8"));
    }

    //ABCDEF
    @Test
    public void maskHandler() throws UnsupportedEncodingException {
        byte[] decoded = new byte[6];
        byte[] encoded = new byte[] {(byte) 198,(byte)  131,(byte)  130, (byte) 182,(byte)  194,(byte)  135};
        byte[] key = {(byte) 167, (byte) 225, (byte) 225,(byte)  210};

        for (int i = 0; i < encoded.length; i++) {
            decoded[i] = (byte)(encoded[i] ^ key[i & 0x3]);
        }
        System.out.println(new String(decoded, "UTF-8"));
    }
}
