package unitTest.http;

import com.jasper.model.HttpRequest;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.models.HttpParser;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 * 5.1.2 Request-URI
 */
public class readURITest {

    private HttpParser parser;

    @Before
    public void createParser() {
        parser = new HttpParser();
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

        assertTrue("Set to Reading HTTP version", state.isReadingHttpVersion());
    }

    /**
     * Check to see which Chars are invalid.
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

        assertTrue("URL Invalid", request.getRequestMethod().isGetRequest());
        assertTrue("Set to ERROR", state.isErrorState());
    }

    @Test
    public void setHttpRequestWithNoEntityExpectingStillReading() {
        String stringToTest = "GET /";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("", request.getPath());
        assertTrue("url reading method is set but not yet finished.", state.isReadingURI());
    }

    @Test
    public void setHttpRequestWithNoPath() {
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
    public void setHttpRequestWithEntityFinishedReading() {
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
    public void setHttpRequestWithEntityAndPortFinishedReading() {
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

    //used as an example for the variables.
    @Test
    public void testURL() throws MalformedURLException {
        URL aURL = new URL("http://example.com:80/docs/books/tutorial"
                + "/index.html?name=networking&test=apple;ok=apple2#DOWNLOADING");

        System.out.println("protocol = " + aURL.getProtocol());
        System.out.println("authority = " + aURL.getAuthority());
        System.out.println("host = " + aURL.getHost());
        System.out.println("port = " + aURL.getPort());
        System.out.println("path = " + aURL.getPath());
        System.out.println("query = " + aURL.getQuery());
        System.out.println("filename = " + aURL.getFile());
        System.out.println("ref = " + aURL.getRef());
    }

    /**
     * One space is only allowed, no URI found otherwise.
     * //(HTTP Protocol is very strict.)on what it receives.
     */
    @Test
    public void setHttpRequestWithSpaces() {
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
