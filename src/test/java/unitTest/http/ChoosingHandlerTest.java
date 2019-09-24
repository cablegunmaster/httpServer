package unitTest.http;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.IRequestHandler;
import com.jasper.model.connection.RequestHandler;
import com.jasper.model.http.HttpResponse;
import com.jasper.model.http.HttpResponseHandler;
import com.jasper.model.http.enums.RequestType;
import com.jasper.model.http.enums.StatusCode;
import com.jasper.model.http.upgrade.UpgradeHttpResponse;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ChoosingHandlerTest {

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

        Map<String, IRequestHandler> socketMap = new HashMap<>();
        socketMap.put("/path", (req, res) -> {
            //read out request.
            //send back with response?
        });

        HttpResponseHandler responseHandler = new RequestHandler(new Controller(
                9999,
                new HashMap<>(),
                new HashMap<>(),
                socketMap,
                false))
                .handleRequest(request);

        assertEquals(UpgradeHttpResponse.class, responseHandler.getClass());
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

        Map<String, IRequestHandler> getMap = new HashMap<>();
        getMap.put("/index", (req, res) -> {
            //read out request.
            //send back with response?
        });

        HttpResponseHandler responseHandler = new RequestHandler(new Controller(
                20801,
                getMap,
                new HashMap<>(),
                new HashMap<>(),
                false))
                .handleRequest(request);

        assertEquals(HttpResponse.class, responseHandler.getClass());
    }
}
