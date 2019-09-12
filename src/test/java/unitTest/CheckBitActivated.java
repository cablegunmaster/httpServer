package unitTest;

import com.jasper.model.request.uriparser.SocketMessageParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple test to see where it read from.
 */
public class CheckBitActivated {

    @Test
    public void testBitActivatedONeOTwentyNine(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(0, 129));
    }

    @Test
    public void testBitActivatedONe(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(0, 1));
    }

    @Test
    public void testBitActivatedONeTestWrong(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertFalse(parser.checkBitActivated(0, 4));
    }

    @Test
    public void testBitActivatedTwo(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(1, 2));
    }

    @Test
    public void testBitActivatedFour(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(2, 4));
    }

    @Test
    public void testBitActivatedEight(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(3, 8));
    }

    @Test
    public void testBitActivatedSixteen(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(4, 16));
    }
    @Test
    public void testBitActivatedThirtyTwo(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(5, 32));
    }
    @Test
    public void testBitActivatedSixtyFour(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(6, 64));
    }
    @Test
    public void testBitActivatedOneHundredTwentyEight(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(7, 128));
    }


    //9th bit?
    @Test
    public void testBitActivatedTwoHundredFiftySix(){
        SocketMessageParser parser = new SocketMessageParser();
        Assert.assertTrue(parser.checkBitActivated(8, 256));
    }
}

