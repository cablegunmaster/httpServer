package unitTest;

import com.jasper.model.request.HttpRequest;
import com.jasper.model.request.RequestParser;
import com.jasper.model.request.requestenums.RequestType;
import org.junit.Before;
import org.junit.Test;

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

    private RequestParser parser;
    private HttpRequest httpRequest;

    @Before
    public void createParser() {
        httpRequest = new HttpRequest();
        parser = new RequestParser();
    }

    @Test
    public void requestMethodIsNotSetAndStillReading() {
        String stringToTest = "GE";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("No method yet found", httpRequest.getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingMethod());
    }

    @Test
    public void requestMethodIsSetTestAlmost() {
        String stringToTest = "GET";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("No method yet found", httpRequest.getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingMethod());
    }

    @Test
    public void stillReading1LetterTest() {
        String stringToTest = "O";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("No method yet found", httpRequest.getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getParseState().isReadingMethod());
    }


    @Test
    public void requestMethodIsSetWithGETTest() {
        String stringToTest = "GET /";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Request method should be set to GET ", httpRequest.getRequestMethod().equals(RequestType.GET));
        assertTrue("Next State should be set as READING URI when found.", parser.getParseState().isReadingURI());
    }

    @Test
    public void requestMethodIsSetWithPOSTTest() {
        String stringToTest = "POST /";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Request method should be set to POST ", httpRequest.getRequestMethod().equals(RequestType.POST));
        assertTrue("Next State should be set as READING URI when found.", parser.getParseState().isReadingURI());
    }

    @Test
    public void invalidOneLetterRequestMethod() {
        String stringToTest = "Z";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Invalid request is found no requestType is set ", httpRequest.getRequestMethod() == null);
        assertTrue("next State should be set as STILL READING?", parser.getParseState().isErrorState());
    }

    @Test
    public void invalidRequestMethod() {
        String stringToTest = "OMG";

        parser.parseRequest(stringToTest, httpRequest);

        assertTrue("Invalid request is found no requestType is set ", httpRequest.getRequestMethod() == null);
        assertTrue("next State should be set as STILL READING?", parser.getParseState().isErrorState());
    }
}
