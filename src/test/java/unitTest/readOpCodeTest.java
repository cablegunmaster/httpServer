package unitTest;

import com.jasper.model.request.uriparser.SocketMessageParser;
import com.jasper.model.socketEnum.OpCode.OpCode;
import org.junit.Assert;
import org.junit.Test;

public class readOpCodeTest {

    @Test
    public void testOpCode() {
        SocketMessageParser parser = new SocketMessageParser();
        parser.setOpcode(0);
        Assert.assertEquals(OpCode.CONTINUATION, parser.getOpCode());
    }

    @Test
    public void testOpCodeTwo() {
        SocketMessageParser parser = new SocketMessageParser();
        parser.setOpcode(10);
        Assert.assertEquals(OpCode.PONG, parser.getOpCode());
    }

    @Test
    public void testByValueOf() {
        Assert.assertEquals(OpCode.PONG, OpCode.findByValue("A"));
        Assert.assertEquals(OpCode.CONTINUATION, OpCode.findByValue("0"));
    }


}
