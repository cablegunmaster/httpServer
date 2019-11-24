package unitTest.bytes;

import com.jasper.model.socket.models.SocketMessageParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class readByteUnsignedSixteenTest {

    //no content just test for length and size for length.

    private List<Integer> oneByteList = Arrays.asList(
            129,
            254,

            8,
            25);

    private List<Integer> twoByteList = Arrays.asList(
            129,
            255,

            8,
            25,
            25,
            25,
            25,
            8,
            25,
            1);

    @Test
    public void testLongerValue() {
        SocketMessageParser parser = new SocketMessageParser();

        for (int i : oneByteList) {
            parser.parseMessage(i);
        }
    }

    @Test
    public void testLongerValuePossibleSixtyFour() {
        SocketMessageParser parser = new SocketMessageParser();

        for (int i : twoByteList) {
            parser.parseMessage(i);
        }
    }
}
