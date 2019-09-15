package unitTest.http;

import com.jasper.model.HttpRequest;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.enums.StateUrl;
import com.jasper.model.http.models.HttpParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class readGETTest {

    private HttpParser parser;

    @Before
    public void createParser() {
        parser = new HttpParser();
    }

    @Test
    public void HttpGETVariableTest() {
        String stringToTest = "GET http://www.google.com/index.php?name=value ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        StateUrl stateUrl = request.getStateUrl();
        HttpState state = request.getState();

        assertNotNull("error state found:" + stateUrl.name(),request.getPath());
        assertEquals("get name=value", "name=value", request.getQuery());
        assertTrue("Reading Method set to ERROR", state.isReadingHttpVersion());
    }

    @Test
    public void httpGETMultipleVariableTest() {
        String stringToTest = "index.php?name=value&test=false ";

        //set STATE.
        parser.getRequest().setState(HttpState.READ_URI);
        parser.getRequest().setStateUrl(StateUrl.READ_PATH);

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        StateUrl stateUrl = request.getStateUrl();
        HttpState state = request.getState();

        assertNotNull("HttpState found:" + stateUrl.name(), parser.getRequest().getPath());
        assertEquals("GET REQUEST: 'name=value&test=false'", "name=value&test=false", request.getQuery());
        assertTrue("Reading Method set to ERROR", state.isReadingHttpVersion());
    }
}
