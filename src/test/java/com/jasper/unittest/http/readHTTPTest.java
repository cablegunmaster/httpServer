package com.jasper.unittest.http;

import com.jasper.model.HttpRequest;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.enums.RequestType;
import com.jasper.model.http.models.HttpRequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class readHTTPTest {

    private HttpRequestParser parser;

    private final String LINE = "\r\n";

    @Before
    public void createParser() {
        parser = new HttpRequestParser();
    }

    @Test
    public void testRequestHTTPStartReading() {
        String stringToTest = "GET /index.html H";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Method GET  found", request.getRequestMethod(), RequestType.GET);
        assertTrue("Is still reading the HTTP version line, waiting end char." +
                "", state.isReadingHttpVersion());
    }

    @Test
    public void testSetHttpRequestWithNoPathHTTPVersionOnePointOne() {
        String stringToTest = "GET http://localhost:8080/ HTTP/1.1"+ LINE;

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Path equals to Slash.", "/", request.getPath());
        assertTrue("url reading method is set and finished.", state.isReadingHeaderName());
    }

    @Test
    public void testRequestHTTPISStillReading() {
        String stringToTest = "GET /index.html HTTP/1.1";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Method GET  found", request.getRequestMethod(), RequestType.GET);
        assertTrue("Is still reading the HTTP version line, waiting for end char." +
                "", state.isReadingHttpVersion());
    }

    @Test
    public void testRequestHTTPIsStartingToReadHeader() {
        String stringToTest = "GET /index.html HTTP/1.1 ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("/index.html", request.getPath());
        assertEquals("Method GET  found", request.getRequestMethod(), RequestType.GET);
        assertTrue("Is reading the first header. [HttpState]:" + state.toString(), state.isReadingHeaderName());
    }


    @Test
    public void testRequestLocalHost() {
        String stringToTest = "GET http://localhost:8080/index.html HTTP/1.1"+ LINE;

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Method GET  found", request.getRequestMethod(), RequestType.GET);
        assertTrue("Is reading the first header. [HttpState]:" + state, state.isReadingHeaderName());
    }
}
