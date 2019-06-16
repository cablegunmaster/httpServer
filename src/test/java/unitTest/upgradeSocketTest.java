package unitTest;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.request.RequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class upgradeSocketTest {

    private RequestParser parser;

    private final String LINE = "\r\n";

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void setHttpUpgradeSocketTest() {

        //I send.
        String stringToTest = "GET /chat HTTP/1.1" + LINE +
                "Host: server.example.com" + LINE +
                "Upgrade: websocket" + LINE +
                "Connection: Upgrade" + LINE +
                "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==" + LINE +
                "Origin: http://example.com" + LINE +
                "Sec-WebSocket-Protocol: chat, superchat" + LINE +
                "Sec-WebSocket-Version: 13"
                + LINE + LINE;

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        StateUrl stateUrl = request.getStateUrl(); //for url reading.
        State state = request.getState();

        //I receive.
        assertNotNull("error state found:" + stateUrl.name(), request.getPath());
        assertTrue("Reading Method set to DONE", state.isDone());
        assertTrue("Request is upgrading connection", request.isUpgradingConnection());
    }

    @Test
    public void returnCorrectWebsocketResponse() {

        //I send.
        String stringToTest = "GET /chat HTTP/1.1" + LINE +
                "Host: server.example.com" + LINE +
                "Upgrade: websocket" + LINE +
                "Connection: Upgrade" + LINE +
                "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==" + LINE +
                "Origin: http://example.com" + LINE +
                "Sec-WebSocket-Protocol: chat, superchat" + LINE +
                "Sec-WebSocket-Version: 13"
                + LINE + LINE;

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        StateUrl stateUrl = request.getStateUrl(); //for url reading.
        State state = request.getState();

        //I receive.
        assertNotNull("error state found:" + stateUrl.name(), request.getPath());
        assertTrue("Reading Method set to DONE", state.isDone());
        assertTrue("Request is upgrading connection", request.isUpgradingConnection());
    }
}
