package com.jasper.unittest.http;

import com.jasper.model.HttpRequest;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.models.HttpRequestParser;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 * 5.1.2 Request-URI
 * This file test the input is an valid HTTP Request in various forms.
 */
public class readURITest {

    private HttpRequestParser parser;

    @Before
    public void createParser() {
        parser = new HttpRequestParser();
    }

    /**
     * Check a valid request.
     */
    @Test
    public void setHttpRequestWithValidUrl() {
        String stringToTest = "GET /index.html h";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }
        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertTrue("Test can read the string properly", state.isReadingHttpVersion());
    }

    /**
     * Check to see if invalid Chars are allowed.
     */
    @Test
    public void setHttpRequestWithInvalidEntity() {
        String stringToTest = "GET /@%@#%*@#() ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertTrue("test Uri is invalid", request.getRequestMethod().isGetRequest());
        assertTrue("Set to ERROR", state.isErrorState());
    }

    @Test
    public void testHttpRequestTestStillReading() {
        String stringToTest = "GET /";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("check the path", request.getPath());
        assertTrue("url reading method is set but not yet finished.", state.isReadingURI());
    }

    @Test
    public void testStateIsReadingHttpVersion() {
        String stringToTest = "GET http://localhost:8080/ ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Path is relative", "/", request.getPath());
        assertTrue("url reading method is set and finished.", state.isReadingHttpVersion());
    }

    @Test
    public void testStateToIsReadingHttpVersion() {
        String stringToTest = "GET / ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNotNull("Relative url found", request.getPath());
        assertTrue("Reading Method set to ERROR", state.isReadingHttpVersion());
    }

    @Test
    public void testStateIsReadingHttpVersionAfterUriWithSpace() {
        String stringToTest = "GET http://www.google.com:8080/index.php?name=value ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNotNull("error state found:" + request.getStateUrl().name(),request.getPath());
        assertEquals("get name=value", "name=value", request.getQuery());
        assertEquals("port is 8080", 8080, (int) request.getPort());
        assertTrue("Reading Method set to ERROR", state.isReadingHttpVersion());
    }

    /**
     * One space is only allowed, no URI found otherwise.
     * //(HTTP Protocol is very strict.)on what it receives.
     */
    @Test
    public void checkNextCharacterIsNotSpace() {
        String stringToTest = "GET  ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("No url yet found", request.getPath());
        assertTrue("Reading Method set to ERROR", state.isErrorState());
    }
}
