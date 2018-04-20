package unitTest;

import com.jasper.model.httpenums.RequestType;
import com.jasper.model.request.RequestParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * for sockets to be able to work on the JS side of the webpage
 * The Headers should be able to be parsed and put in a nice Map.
 * ex: https://code.tutsplus.com/tutorials/http-headers-for-dummies--net-8039
 */
public class readHeadersTest {

    private RequestParser parser;

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void requestHTTPISStillReading() {
        String stringToTest = "GET /tutorials/other/top-20-mysql-best-practices/ HTTP/1.1\r\n" +
                "Host: net.tutsplus.com\r\n" +
                "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR " +
                "3.5.30729)\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                "Accept-Language: en-us,en;q=0.5\r\n" +
                "Accept-Encoding: gzip,deflate\r\n" +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n" +
                "Keep-Alive: 300\r\n" +
                "Connection: keep-alive\r\n" +
                "Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache" +
                "\r\n\r\n";

        for (int i = 0; i < stringToTest.length(); i++){
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        assertEquals("Method GET  found", parser.getRequest().getRequestMethod(), RequestType.GET);
        assertTrue("State:" + parser.getRequest().getState().name() +
                "",  parser.getRequest().getState().isDone());
        assertEquals(11, parser.getRequest().getHeaders().size());
        assertEquals("no-cache",parser.getRequest().getHeaders().get("Pragma"));
        assertEquals("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR " +
                "3.5.30729)",parser.getRequest().getHeaders().get("User-Agent"));
    }
}
