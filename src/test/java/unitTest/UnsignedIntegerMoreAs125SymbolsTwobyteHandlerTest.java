package unitTest;

import com.jasper.model.response.SocketResponse;
import org.junit.Assert;
import org.junit.Test;

public class UnsignedIntegerMoreAs125SymbolsTwobyteHandlerTest {

    private final String test = "testtesttetesttesttetesttesttetesttesttesttetesttesttesttetesttesttesttetesttesttesttetesttesttesttesttetesttesttesttesttetesttesttesttesttetesttesttesttesttetesttesttesttetestt";

    //166 length
    private final String testTwo = "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcacabcabcabcabcabcabcabcacabcabcabcabcabcabcabcacabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabca";
    //-90 , -1 ??


    @Test
    public void testLength(){
        Assert.assertEquals(test.length(), 177);
    }

    @Test
    public void testLengthTwo(){
        Assert.assertEquals(testTwo.length(), 177);
    }

    @Test
    public void testTWOByteHandler(){
        SocketResponse response = new SocketResponse();
        byte[] byteSize  = new byte[4];
        response.shortToByteArray(255, byteSize);

        System.out.println(byteSize[2] + " "+ byteSize[3]);

        String s1 = byteToString(byteSize[2]);
        String s2 = byteToString(byteSize[3]);

        System.out.println(s2 + " " + s1);
    }

    @Test
    public void testTwoByteHandler(){
        SocketResponse response = new SocketResponse();
        byte[] byteSize  = new byte[4];
        response.shortToByteArray(512, byteSize);

        System.out.println(byteSize[2] + " "+ byteSize[3]);

        String s1 = byteToString(byteSize[2]);
        String s2 = byteToString(byteSize[3]);

        System.out.println(s1 + " " + s2);
    }

    @Test
    public void testLengthToNumber(){
        SocketResponse response = new SocketResponse();
        byte[] byteSize  = new byte[4];
        response.shortToByteArray(177, byteSize);

        System.out.println(byteSize[2] + " "+ byteSize[3]);

        String s1 = byteToString(byteSize[2]);
        String s2 = byteToString(byteSize[3]);

        System.out.println(s1 + " " + s2);
    }

    private String byteToString(byte b){
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
}
