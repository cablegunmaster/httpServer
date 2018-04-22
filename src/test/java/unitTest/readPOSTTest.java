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
    public void HttpPOSTVariableTest() {
        String stringToTest = "POST /foo.php HTTP/1.1\n" +
                "Host: localhost\n" +
                "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                "Accept-Language: en-us,en;q=0.5\n" +
                "Accept-Encoding: gzip,deflate\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" +
                "Keep-Alive: 300\n" +
                "Connection: keep-alive\n" +
                "Referer: http://localhost/test.php\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Content-Length: 43\n" +
                " \n" +
                "first_name=John&last_name=Doe&action=Submit";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertNotNull("error state found:" +"/foo.php", parser.getRequest().getPath());
        assertEquals("BODY: ", 3, parser.getRequest().getQueryPOST().size());
        assertTrue("Reading Method set to DONE", parser.getRequest().getState().isDone());
    }

    @Test
    public void httpPOSTMultipleVariableTest() {
        String stringToTest = "POST /foo.php HTTP/1.1\n" +
                "Host: localhost\n" +
                "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n" +
                "Accept-Language: en-us,en;q=0.5\n" +
                "Accept-Encoding: gzip,deflate\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" +
                "Keep-Alive: 300\n" +
                "Connection: keep-alive\n" +
                "Referer: http://localhost/test.php\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Content-Length: 43\n" +
                " \n" +
                "first_name=John&last_name=Doe&action=Submit";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertNotNull("error state found:" +"/foo.php", parser.getRequest().getPath());
        assertEquals("BODY: ", 3, parser.getRequest().getQueryPOST().size());
        assertTrue("Reading Method set to DONE", parser.getRequest().getState().isDone());
    }
}
