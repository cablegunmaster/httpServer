package unitTest;

import com.jasper.model.request.RequestParser;
import com.jasper.model.request.requestenums.RequestType;
import org.junit.Before;
import org.junit.Test;

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

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Method GET  found", parser.getRequest().getRequestMethod().equals(RequestType.GET));
       // assertTrue("URL is valid", parser.getRequest().getRequestpath().equals("/index.html"));
        assertTrue("Is still reading the HTTP version line, waiting end char." +
                "",  parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void requestHTTPISStillReading() {
        String stringToTest = "GET /index.html HTTP/1.1";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }


        assertTrue("Method GET  found", parser.getRequest().getRequestMethod().equals(RequestType.GET));
       // assertTrue("URL is valid", parser.getRequest().getRequestpath().equals("/index.html"));
        assertTrue("Is still reading the HTTP version line, waiting for end char." +
                "",  parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void requestHTTPIsStartingToReadHeader() {
        String stringToTest = "GET /index.html HTTP/1.1 \\r\\n";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Method GET  found", parser.getRequest().getRequestMethod().equals(RequestType.GET));
      //  assertTrue("URL is valid", parser.getRequest().getRequestpath().equals("/index.html"));
        assertTrue("Is reading the first header.",  parser.getRequest().getState().isReadingHeaderName());
    }
}
