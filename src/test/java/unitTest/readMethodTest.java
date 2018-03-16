package unitTest;

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

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void requestMethodIsNotSetAndStillReading() {
        String stringToTest = "GE";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("No method yet found", parser.getRequest().getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getRequest().getState().isReadingMethod());
    }

    @Test
    public void requestMethodIsSetTestAlmost() {
        String stringToTest = "GET";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("No method yet found", parser.getRequest().getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getRequest().getState().isReadingMethod());
    }

    @Test
    public void stillReading1LetterTest() {
        String stringToTest = "O";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("No method yet found", parser.getRequest().getRequestMethod() == null);
        assertTrue("Reading Method still not yet finished", parser.getRequest().getState().isReadingMethod());
    }


    @Test
    public void requestMethodIsSetWithGETTest() {
        String stringToTest = "GET /";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Request method should be set to GET ", parser.getRequest().getRequestMethod().equals(RequestType.GET));
        assertTrue("Next State should be set as READING URI when found.", parser.getRequest().getState().isReadingURI());
    }

    @Test
    public void requestMethodIsSetWithPOSTTest() {
        String stringToTest = "POST /";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Request method should be set to POST ",  parser.getRequest().getRequestMethod().equals(RequestType.POST));
        assertTrue("Next State should be set as READING URI when found.", parser.getRequest().getState().isReadingURI());
    }

    @Test
    public void invalidOneLetterRequestMethod() {
        String stringToTest = "Z";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Invalid request is found no requestType is set ",parser.getRequest().getRequestMethod() == null);
        assertTrue("next State should be set as STILL READING", parser.getRequest().getState().isReadingMethod());
    }

    @Test
    public void invalidRequestMethod() {
        String stringToTest = "OMG ";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertTrue("Invalid request is found no requestType is set ", parser.getRequest().getRequestMethod() == null);
        assertTrue("next State should be set as Error", parser.getRequest().getState().isErrorState());
    }
}
