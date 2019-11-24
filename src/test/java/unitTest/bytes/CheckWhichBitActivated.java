package unitTest.bytes;

import org.junit.Assert;
import org.junit.Test;

import static com.jasper.model.socket.models.utils.ByteUtil.checkBitActivated;

/**
 * Simple test to see where it read from.
 */
public class CheckWhichBitActivated {
    
    @Test
    public void testBitActivatedONeOTwentyNine() {
        Assert.assertTrue(checkBitActivated(0, 129));
    }

    @Test
    public void testBitActivatedONe() {
        Assert.assertTrue(checkBitActivated(0, 1));
    }

    @Test
    public void testBitActivatedONeTestWrong() {
        Assert.assertFalse(checkBitActivated(0, 4));
    }

    @Test
    public void testBitActivatedTwo() {
        Assert.assertTrue(checkBitActivated(1, 2));
    }

    @Test
    public void testBitActivatedFour() {
        Assert.assertTrue(checkBitActivated(2, 4));
    }

    @Test
    public void testBitActivatedEight() {
        Assert.assertTrue(checkBitActivated(3, 8));
    }

    @Test
    public void testBitActivatedSixteen() {
        Assert.assertTrue(checkBitActivated(4, 16));
    }

    @Test
    public void testBitActivatedThirtyTwo() {
        Assert.assertTrue(checkBitActivated(5, 32));
    }

    @Test
    public void testBitActivatedSixtyFour() {
        Assert.assertTrue(checkBitActivated(6, 64));
    }

    @Test
    public void testBitActivatedOneHundredTwentyEight() {
        Assert.assertTrue(checkBitActivated(7, 128));
    }


    //9th bit?
    @Test
    public void testBitActivatedTwoHundredFiftySix() {
        Assert.assertTrue(checkBitActivated(8, 256));
    }
}

