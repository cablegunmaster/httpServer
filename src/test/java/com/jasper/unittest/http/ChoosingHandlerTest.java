package com.jasper.unittest.http;

import com.jasper.controller.Controller;
import com.jasper.model.HttpRequest;
import com.jasper.model.IRequestBuilder;
import com.jasper.model.connection.HttpRequestHandler;
import com.jasper.model.http.HttpResponse;
import com.jasper.model.http.HttpResponseBuilder;
import com.jasper.model.http.enums.RequestType;
import com.jasper.model.http.enums.StatusCode;
import com.jasper.model.http.upgrade.UpgradeHttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ChoosingHandlerTest {

    /**
     * Check if correct response is given.
     * Minimum to get a server running with a /path as socket.
     */
    @Test
    public void HttpGetSocketResponseHandlerNoSocketHandlerFoundTest() throws IOException {
        HttpRequest request = new HttpRequest(new Socket());
        request.setPath("/path");
        request.setStatusCode(StatusCode.SWITCHING_PROTOCOL);
        request.setRequestMethod(RequestType.GET);

        Map<String, IRequestBuilder> socketMap = new HashMap<>();
        socketMap.put("/path", (req, res) -> {
            //read out request.
            //send back with response?
        });

        HttpResponseBuilder responseHandler = new HttpRequestHandler(new Controller(
                9999,
                new HashMap<>(),
                new HashMap<>(),
                socketMap,
                false))
                .handleHttpRequest(request);

        assertEquals(UpgradeHttpResponse.class, responseHandler.getClass());
    }

    /**
     * Check if correct HTTPResponse is given.
     * Minimum Server with a /index as path.
     */
    @Test
    public void HttpGetHttpResponseHandlerTest() throws IOException {
        HttpRequest request = new HttpRequest(new Socket());
        request.setPath("/index");
        request.setStatusCode(StatusCode.OK);
        request.setRequestMethod(RequestType.GET);

        Map<String, IRequestBuilder> getMap = new HashMap<>();
        getMap.put("/index", (req, res) -> {
            //read out request.
            //send back with response?
        });

        HttpResponseBuilder responseHandler = new HttpRequestHandler(new Controller(
                20801,
                getMap,
                new HashMap<>(),
                new HashMap<>(),
                false))
                .handleHttpRequest(request);

        assertEquals(HttpResponse.class, responseHandler.getClass());
    }
}
