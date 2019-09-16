package unitTest.socket;

import com.jasper.model.socket.models.entity.Frame;
import com.jasper.model.socket.enums.OpCode;
import org.junit.Assert;
import org.junit.Test;

public class readOpCodeTest {

    private Frame f;

    @Test
    public void testOpCode() {
        f = new Frame();
        f.setOpcode(0);
        Assert.assertEquals(OpCode.CONTINUATION, f.getOpCode());
    }

    @Test
    public void testOpCodeTwo() {
        f = new Frame();
        f.setOpcode(10);
        Assert.assertEquals(OpCode.PONG, f.getOpCode());
    }

    @Test
    public void testByValueOf() {
        Assert.assertEquals(OpCode.PONG, OpCode.findByValue("A"));
        Assert.assertEquals(OpCode.CONTINUATION, OpCode.findByValue("0"));
    }


}
