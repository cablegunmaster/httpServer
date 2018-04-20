package unitTest;

import com.jasper.model.request.RequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class readPOSTTest {

    private RequestParser parser;

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void HttpGETVariableTest() {
        String stringToTest = "GET http://www.google.com/index.php?name=value ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertNotNull("error state found:" + parser.getRequest().getStateUrl().name(), parser.getRequest().getPath());
        assertEquals("get name=value", "name=value", parser.getRequest().getQuery());
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isReadingHttpVersion());
    }

    @Test
    public void httpGETMultipleVariableTest() {
        String stringToTest = "GET http://www.google.com/index.php?name=value&test=false ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertNotNull("error state found:" + parser.getRequest().getStateUrl().name(), parser.getRequest().getPath());
        assertEquals("GET REQUEST: 'name=value&test=false'", "name=value&test=false", parser.getRequest().getQuery());
        assertTrue("Reading Method set to ERROR", parser.getRequest().getState().isReadingHttpVersion());
    }
}
