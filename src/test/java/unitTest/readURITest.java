package unitTest;

import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.RequestParser;
import com.jasper.model.request.requestenums.ParseState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 * 5.1.2 Request-URI
 */
public class readURITest {

    private RequestParser parser;
    private HttpRequest httpRequest;

    @Before
    public void createParser() {
        httpRequest = new HttpRequest();
        parser = new RequestParser();
    }

    /**
     * Check a valid request.
     */
    @Test
    public void setHttpRequestWithValidUrl() {
        String stringToTest = "GET /index.html h";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("URL index page found", httpRequest.getRequestpath() != null);
        assertTrue("Set to Reading HTTP version", parser.getParseState().isReadingHttpVersion());
    }

    /**
     * Check to see which Chars are invalid.
     */
    @Test
    public void setHttpRequestWithInvalidEntity() {
        String stringToTest = "GET /@%@#%*@#()";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("URL Invalid", httpRequest.getRequestMethod() == null);
        assertTrue("Set to ERROR", parser.getParseState().isErrorState());
    }

    @Test
    public void setHttpRequestWithNoEntityExpectingStillReading() {
        String stringToTest = "GET /";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("url found but not yet finished. ", httpRequest.getRequestpath().equals("/"));
        assertTrue("Set to Reading HTTP URI", parser.getParseState().isReadingURI());
    }

    //One space is only allowed.
    @Test
    public void setHttpRequestWithSpaces() {
        String stringToTest = "GET  ";

        parser.setParseState(ParseState.READING_METHOD);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("No url yet found", httpRequest.getRequestMethod() == null);
        assertTrue("Reading Method set to ERROR", parser.getParseState().isErrorState());
    }
}
