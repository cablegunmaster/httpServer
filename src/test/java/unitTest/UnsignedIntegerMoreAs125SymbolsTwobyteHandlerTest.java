package unitTest;

import com.jasper.model.response.SocketResponse;
import org.junit.Assert;
import org.junit.Test;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

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
    public void incomingNumberToInt(){
        // - 90  is signed
        // Create an empty ByteBuffer with a 10 byte capacity
//        ByteBuffer bbuf = ByteBuffer.allocate(2);
//        bbuf.putInt((int) -90);

        //System.out.println(bbuf.toString());

        System.out.println(ByteBuffer.allocate(4).putInt((10000)).array());


    }

    private String byteToString(byte b){
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
}
