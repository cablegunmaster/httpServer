package com.jasper.unittest.http;

import com.jasper.model.HttpRequest;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.models.HttpRequestParser;
import com.jasper.model.http.enums.RequestType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test to see if the unit:
 * ReadMethod would actually return a RequestType.
 *
 * Conform to: https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 * 5.1.1 Method
 *
 * To test if the transition :
 *  READ_METHOD to READ_URI works.
 *  READ_METHOD to ERROR works.
 */
public class readMethodTest {

    private HttpRequestParser parser;

    @Before
    public void createParser() {
        parser = new HttpRequestParser();
    }

    @Test
    public void testRequestMethodIsNotSetAndStillReading() {
        String stringToTest = "GE";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("No method yet found", request.getRequestMethod());
        assertTrue("Reading Method still not yet finished", state.isReadingMethod());
    }

    @Test
    public void testRequestMethodIsSetTestAlmost() {
        String stringToTest = "GET";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("No method yet found", request.getRequestMethod());
        assertTrue("Reading Method still not yet finished", state.isReadingMethod());
    }

    @Test
    public void testStillReading1Letter() {
        String stringToTest = "O";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("No method yet found", request.getRequestMethod());
        assertTrue("Reading Method still not yet finished", state.isReadingMethod());
    }


    @Test
    public void testRequestMethodIsSetWithGETTest() {
        String stringToTest = "GET /";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Request method should be set to GET ", request.getRequestMethod(), RequestType.GET);
        assertTrue("Next HttpState should be set as READING URI when found.", state.isReadingURI());
    }

    @Test
    public void requestMethodIsSetWithPOSTTest() {
        String stringToTest = "POST /";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Request method should be set to POST ", request.getRequestMethod(), RequestType.POST);
        assertTrue("Next HttpState should be set as READING URI when found.", state.isReadingURI());
    }

    @Test
    public void testInvalidOneLetterRequestMethod() {
        String stringToTest = "Z";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("Invalid request is found no requestType is set ", request.getRequestMethod());
        assertTrue("next HttpState should be set as STILL READING", state.isReadingMethod());
    }

    @Test
    public void testInvalidRequestMethod() {
        String stringToTest = "OMG ";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("Invalid request is found no requestType is set ", request.getRequestMethod());
        assertTrue("next HttpState should be set as Error", state.isErrorState());
    }
}
