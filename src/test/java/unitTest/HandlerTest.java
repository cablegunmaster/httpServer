package unitTest;

import com.jasper.controller.Controller;
import com.jasper.model.Client;
import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.StatusCode;
import com.jasper.model.response.HttpResponse;
import com.jasper.model.response.HttpResponseHandler;
import com.jasper.model.response.SocketSwitchingResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HandlerTest {

    /**
     * Check if correct response is given.
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void HttpGetSocketResponseHandlerTest() throws UnsupportedEncodingException {
        HttpRequest request = new HttpRequest();
        request.setUpgradingConnection(true);
        request.setStatusCode(StatusCode.SWITCHING_PROTOCOL);
        request.setRequestMethod(RequestType.GET);

        Client client = new Client(null, new Controller(8080, new HashMap<>(), new HashMap<>(), false));
        HttpResponseHandler responseHandler = client.handleRequestHandlers(request);

        assertEquals(responseHandler.getClass(), SocketSwitchingResponse.class);
    }

    /**
     * Check if correct HTTPResponse is given.
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void HttpGetHttpResponseHandlerTest() throws UnsupportedEncodingException {
        HttpRequest request = new HttpRequest();
        request.setStatusCode(StatusCode.OK);
        request.setRequestMethod(RequestType.GET);

        Client client = new Client(null, new Controller(8080, new HashMap<>(), new HashMap<>(), false));
        HttpResponseHandler responseHandler = client.handleRequestHandlers(request);

        assertEquals(responseHandler.getClass(), HttpResponse.class);
    }
}
