package com.jasper.unittest.socket;

import com.jasper.model.socket.enums.OPCode;
import com.jasper.model.socket.models.entity.Frame;
import org.junit.Assert;
import org.junit.Test;

public class readOpCodeTest {

    private Frame f;

    @Test
    public void testOpCode() {
        f = new Frame();
        f.setOpCode(0);
        Assert.assertEquals(OPCode.CONTINUATION, f.getOpCode());
    }

    @Test
    public void testOpCodeTwo() {
        f = new Frame();
        f.setOpCode(10);
        Assert.assertEquals(OPCode.PONG, f.getOpCode());
    }
}
