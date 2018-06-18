package unitTest;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.RequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class upgradeSocketTest {

    private RequestParser parser;

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void HttpGETVariableTest() {
        String stringToTest = "GET /chat HTTP/1.1\n" +
                "        Host: server.example.com\n" +
                "        Upgrade: websocket\n" +
                "        Connection: Upgrade\n" +
                "        Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\n" +
                "        Origin: http://example.com\n" +
                "        Sec-WebSocket-Protocol: chat, superchat\n" +
                "        Sec-WebSocket-Version: 13" +
                "\n\n";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        StateUrl stateUrl = request.getStateUrl(); //for url reading.
        State state = request.getState();
        StatusCode statusCode = request.getStatusCode(); //should have set a statusCode! yah fool!

        assertNotNull("error state found:" + stateUrl.name(), request.getPath());
        assertTrue("Reading Method set to DONE", state.isDone());
        assertEquals(StatusCode.SWITCHING_PROTOCOL.name(), statusCode.name());
    }
}
