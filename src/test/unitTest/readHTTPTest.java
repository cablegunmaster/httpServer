package unitTest;

import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.RequestParser;
import com.jasper.model.request.requestenums.RequestType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class readHTTPTest {

    private RequestParser parser;
    private HttpRequest httpRequest;

    @Before
    public void createParser() {
        httpRequest = new HttpRequest();
        parser = new RequestParser();
    }

    @Test
    public void requestHTTPStartReading() {
        String stringToTest = "GET /index.html H";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Method GET  found", httpRequest.getRequestMethod().equals(RequestType.GET));
        assertTrue("URL is valid", httpRequest.getRequestpath().equals("/index.html"));
        assertTrue("Is still reading the HTTP version line, waiting end char." +
                "", parser.getParseState().isReadingHttpVersion());
    }

    @Test
    public void requestHTTPISStillReading() {
        String stringToTest = "GET /index.html HTTP/1.1";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Method GET  found", httpRequest.getRequestMethod().equals(RequestType.GET));
        assertTrue("URL is valid", httpRequest.getRequestpath().equals("/index.html"));
        assertTrue("Is still reading the HTTP version line, waiting for end char." +
                "", parser.getParseState().isReadingHttpVersion());
    }

    @Test
    public void requestHTTPIsStartingToReadHeader() {
        String stringToTest = "GET /index.html HTTP/1.1 \\r\\n";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Method GET  found", httpRequest.getRequestMethod().equals(RequestType.GET));
        assertTrue("URL is valid", httpRequest.getRequestpath().equals("/index.html"));
        assertTrue("Is reading the first header." +
                "", parser.getParseState().isReadingHeaderKey());
    }
}
