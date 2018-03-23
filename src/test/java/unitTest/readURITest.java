package unitTest;

import com.jasper.model.request.RequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 * 5.1.2 Request-URI
 */
public class readURITest {

    private RequestParser parser;

    @Before
    public void createParser() {
        parser = new RequestParser();
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

        assertTrue("URL index page found", parser.getRequest().getgetRequestpath() != null);
        assertTrue("Set to Reading HTTP version", parser.getRequest().getState().isReadingHttpVersion());
    }

    /**
     * Check to see which Chars are invalid.
     */
    @Test
    public void setHttpRequestWithInvalidEntity() {
        String stringToTest = "GET /@%@#%*@#()";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("URL Invalid", parser.getRequest().getRequestMethod() == null);
        assertTrue("Set to ERROR", parser.getRequest().getState().isErrorState());
    }

    @Test
    public void setHttpRequestWithNoEntityExpectingStillReading() {
        String stringToTest = "GET /";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("", parser.getRequest().getRequestpath() == null);
        assertTrue("url reading methos is set but not yet finished.", parser.getRequest().getState().isReadingURI());
    }

    //One space is only allowed.
    @Test
    public void setHttpRequestWithSpaces() {
        String stringToTest = "GET  ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("No url yet found", parser.getRequest().getRequestpath() == null);
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isErrorState());
    }
}
