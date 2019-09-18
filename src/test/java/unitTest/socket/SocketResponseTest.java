package unitTest.socket;

import com.jasper.model.socket.enums.OpCode;
import com.jasper.model.socket.models.SocketResponse;
import org.junit.Test;

import java.util.Arrays;

public class SocketResponseTest {

    @Test
    public void testCreateSocket() {
        byte[] byteArray = SocketResponse.createSocketResponse("Small Message", OpCode.TEXT);
        int[] intArray = new int[byteArray.length];

        // converting byteArray to intArray
        for (int i = 0; i < byteArray.length; intArray[i] = byteArray[i++]) {
            System.out.println(Arrays.toString(intArray));
        }
    }
}
