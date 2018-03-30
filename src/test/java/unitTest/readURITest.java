package unitTest;

import com.jasper.model.request.RequestParser;
import java.net.MalformedURLException;
import java.net.URL;
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

        assertTrue("Set to Reading HTTP version", parser.getRequest().getState().isReadingHttpVersion());
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

        assertTrue("", parser.getRequest().getPath() == null);
        assertTrue("url reading methos is set but not yet finished.", parser.getRequest().getState().isReadingURI());
    }

    @Test
    public void setHttpRequestWithEntityFinishedReading() {
        String stringToTest = "GET / ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Relative url found", parser.getRequest().getPath() != null);
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithEntityAndPortFinishedReading() {
        String stringToTest = "GET http://www.google.com:8080/index.php?name=value ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("error state found:" + parser.getRequest().getStateUrl().name(), parser.getRequest().getPath() != null);
        assertTrue("get name=value", parser.getRequest().getQuery().equals("name=value"));
        assertTrue("port is 8080", parser.getRequest().getPort() == 8080);
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isReadingHttpVersion());
    }


    @Test
    public void setHttpRequestWithEntityDoneReadingAndGETVariable() {
        String stringToTest = "GET http://www.google.com/index.php?name=value ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("error state found:" + parser.getRequest().getStateUrl().name(), parser.getRequest().getPath() != null);
        assertTrue("get name=value", parser.getRequest().getQuery().equals("name=value"));
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithEntityDoneReadingAndMultipleGETVariables() {
        String stringToTest = "GET http://www.google.com/index.php?name=value&test=false ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("error state found:" + parser.getRequest().getStateUrl().name(), parser.getRequest().getPath() != null);
        assertTrue("get name=value&test=false", parser.getRequest().getQuery().equals("name=value&test=false"));
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isReadingHttpVersion());
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

        assertTrue("No url yet found", parser.getRequest().getPath() == null);
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isErrorState());
    }
}
