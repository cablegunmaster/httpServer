package unitTest.bytes;

import com.jasper.model.socket.models.SocketResponse;
import org.junit.Assert;
import org.junit.Test;

public class UnsignedIntegerMoreAs125SymbolsTwobyteHandlerTest {

    private final String test = "testtesttetesttesttetesttesttetesttesttesttetesttesttesttetesttesttesttetesttesttesttetesttesttesttesttetesttesttesttesttetesttesttesttesttetesttesttesttesttetesttesttesttetestt";

    //166 length
    private final String testTwo = "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcacabcabcabcabcabcabcabcacabcabcabcabcabcabcabcacabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabca";

    @Test
    public void testLength(){
        Assert.assertEquals(test.length(), 177);
    }

    @Test
    public void testLengthTwo(){
        Assert.assertEquals(testTwo.length(), 166);
    }

    @Test
    public void testByteHandler(){
        byte[] byteSize  = new byte[4];
        SocketResponse.shortToByteArray("abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcacabcabcabcabcabcabcabcacabcabcabcabcabcabcabcacabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabcacabcabcabcabca".length(), byteSize);

        System.out.println(byteSize[2] + " "+ byteSize[3]);

        String s1 = byteToString(byteSize[2]);
        String s2 = byteToString(byteSize[3]);

        System.out.println(s1 + " " + s2);
    }

    @Test
    public void testByteHandlerBIGGESTNUMBER(){
        byte[] byteSize  = new byte[4];
        SocketResponse.shortToByteArray(65535, byteSize);

        System.out.println(byteSize[2] + " "+ byteSize[3]);

        String s1 = byteToString(byteSize[2]);
        String s2 = byteToString(byteSize[3]);

        System.out.println(s1 + " " + s2);
    }

    private String byteToString(byte b){
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
}
