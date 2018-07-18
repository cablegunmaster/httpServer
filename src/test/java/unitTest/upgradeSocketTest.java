package unitTest;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.State;
import com.jasper.model.httpenums.StateUrl;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.RequestParser;
import com.jasper.model.response.HttpResponse;
import com.jasper.model.response.HttpResponseHandler;
import com.jasper.model.response.SocketSwitchingResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
    public void setHttpUpgradeSocketTest() {

        //I send.
        String stringToTest = "GET /chat HTTP/1.1\n" +
                "Host: server.example.com\n" +
                "Upgrade: websocket\n" +
                "Connection: Upgrade\n" +
                "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\n" +
                "Origin: http://example.com\n" +
                "Sec-WebSocket-Protocol: chat, superchat\n" +
                "Sec-WebSocket-Version: 13" +
                "\n\n";

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
    public void returnCorrectWebsocketResponse(){

        //I send.
        String stringToTest = "GET /chat HTTP/1.1\n" +
                "Host: server.example.com\n" +
                "Upgrade: websocket\n" +
                "Connection: Upgrade\n" +
                "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\n" +
                "Origin: http://example.com\n" +
                "Sec-WebSocket-Protocol: chat, superchat\n" +
                "Sec-WebSocket-Version: 13" +
                "\n\n";

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

    //TODO fix the error when upgrade is different as "websocket" what to reply?
    //TODO fix the correct reponse to the websocket and check why its not working yet.
    //TODO Readup the sec-websocket-key and what to return whenthe key is given.
}
