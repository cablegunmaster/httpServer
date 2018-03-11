package unitTest;

import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.RequestParser;
import com.jasper.model.request.requestenums.ParseState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class readURITest {

    private RequestParser parser;
    private HttpRequest httpRequest;

    @Before
    public void createParser() {
        httpRequest = new HttpRequest();
        parser = new RequestParser();
    }

    @Test
    public void setHttpRequestWithValidUrl() {
        String stringToTest = "GET /index.html";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("No url yet found", httpRequest.getRequestMethod() == httpRequest.getRequestMethod());
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithInvalidEntity() {
        String stringToTest = "GET /@%@#%*@#()";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("URL Invalid", httpRequest.getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingMethod());
    }

    @Test
    public void setHttpRequestWithNoEntityExpectingIndex() {
        String stringToTest = "GET /";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("url found", httpRequest.getRequestpath().equals("index.html"));
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithSpaces() {
        String stringToTest = "GET   ";

        parser.setParseState(ParseState.READING_URI);
        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("No url yet found", httpRequest.getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingMethod());
    }
}
