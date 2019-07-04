package unitTest;

import com.jasper.model.HttpRequest;
import com.jasper.model.httpenums.RequestType;
import com.jasper.model.httpenums.HttpState;
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

    private final String LINE = "\r\n";

    @Before
    public void createParser() {
        parser = new RequestParser();
    }

    @Test
    public void requestHTTPISStillReading() {
        String stringToTest = "GET /tutorials/other/top-20-mysql-best-practices/ HTTP/1.1"+ LINE +
                "Host: net.tutsplus.com"+ LINE +
                "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR " +
                "3.5.30729)"+ LINE +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"+ LINE +
                "Accept-Language: en-us,en;q=0.5"+ LINE +
                "Accept-Encoding: gzip,deflate"+ LINE +
                "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7"+ LINE +
                "Keep-Alive: 300"+ LINE +
                "Connection: keep-alive"+ LINE +
                "Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120"+ LINE +
                "Pragma: no-cache"+ LINE +
                "Cache-Control: no-cache" +
                LINE + LINE;

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        //request all variables needed for easier reading.
        HttpRequest request = parser.getRequest();
        RequestType requestType = request.getRequestMethod();
        HttpState state = request.getState();

        assertEquals("Method GET  found", requestType, RequestType.GET);
        assertTrue("HttpState:" + state.name(), state.isDone());
        assertEquals(11, request.getHeaders().size());
        assertEquals("no-cache", request.getHeaders().get("Pragma"));
        assertEquals("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR " +
                "3.5.30729)", request.getHeaders().get("User-Agent"));
    }
}
