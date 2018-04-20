package unitTest;

import com.jasper.model.httpenums.RequestType;
import com.jasper.model.request.RequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class readHTTPTest {

    private RequestParser parser;

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void requestHTTPStartReading() {
        String stringToTest = "GET /index.html H";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertEquals("Method GET  found", parser.getRequest().getRequestMethod(), RequestType.GET);
        assertTrue("Is still reading the HTTP version line, waiting end char." +
                "", parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithNoPathHTTPVersionOnePointOne() {
        String stringToTest = "GET http://localhost:8080/ HTTP/1.1\\r\\n ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertEquals("Path equals to Slash.", "/", parser.getRequest().getPath());
        assertTrue("url reading method is set and finished.", parser.getRequest().getState().isReadingHeaderName());
    }

    @Test
    public void requestHTTPISStillReading() {
        String stringToTest = "GET /index.html HTTP/1.1";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertEquals("Method GET  found", parser.getRequest().getRequestMethod(), RequestType.GET);
        assertTrue("Is still reading the HTTP version line, waiting for end char." +
                "", parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void requestHTTPIsStartingToReadHeader() {
        String stringToTest = "GET /index.html HTTP/1.1 \\r\\n ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertEquals("/index.html", parser.getRequest().getPath());
        assertEquals("Method GET  found", parser.getRequest().getRequestMethod(), RequestType.GET);
        assertTrue("Is reading the first header. [State]:" + parser.getRequest().getState(), parser.getRequest().getState()
                .isReadingHeaderName());
    }


    @Test
    public void requestLocalHost() {
        String stringToTest = "GET http://localhost:8080/index.html HTTP/1.1 \\r\\n ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertEquals("Method GET  found", parser.getRequest().getRequestMethod(), RequestType.GET);
        assertTrue("Is reading the first header. [State]:" + parser.getRequest().getState(), parser.getRequest().getState()
                .isReadingHeaderName());
    }
}
