package unitTest;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.request.RequestHandler;
import com.jasper.model.response.HttpResponse;
import com.jasper.model.response.HttpResponseHandler;
import com.jasper.model.response.SocketSwitchingResponse;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HandlerTest {

    /**
     * Check if correct response is given.
     * Minimum to get a server running with a /path as socket.
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void HttpGetSocketResponseHandlerNoSocketHandlerFoundTest() throws UnsupportedEncodingException {
        HttpRequest request = new HttpRequest();
        request.setPath("/path");
        request.setUpgradingConnection(true);
        request.setStatusCode(StatusCode.SWITCHING_PROTOCOL);
        request.setRequestMethod(RequestType.GET);

        Map<String, RequestHandler> socketMap = new HashMap<>();
        socketMap.put("/path", (req, res) -> {
            //read out request.
            //send back with response?
        });

        Client client = new Client(null, new Controller(8080, new HashMap<>(), new HashMap<>(), socketMap, false));
        HttpResponseHandler responseHandler = client.handleRequestHandlers(request);
        assertEquals(SocketSwitchingResponse.class, responseHandler.getClass());
    }

    /**
     * Check if correct HTTPResponse is given.
     * Minimum Server with a /index as path.
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void HttpGetHttpResponseHandlerTest() throws UnsupportedEncodingException {
        HttpRequest request = new HttpRequest();
        request.setPath("/index");
        request.setStatusCode(StatusCode.OK);
        request.setRequestMethod(RequestType.GET);

        Map<String, RequestHandler> getMap = new HashMap<>();
        getMap.put("/index", (req, res) -> {
            //read out request.
            //send back with response?
        });

        Client client = new Client(null, new Controller(8080, getMap, new HashMap<>(), new HashMap<>(), false));
        HttpResponseHandler responseHandler = client.handleRequestHandlers(request);

        assertEquals(HttpResponse.class, responseHandler.getClass());
    }
}
