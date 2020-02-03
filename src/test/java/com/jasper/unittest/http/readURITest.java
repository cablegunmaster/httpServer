package com.jasper.unittest.http;

import com.jasper.model.HttpRequest;
import com.jasper.model.http.enums.HttpState;
import com.jasper.model.http.models.HttpParser;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
 * 5.1.2 Request-URI
 */
public class readURITest {

    private HttpParser parser;

    @Before
    public void createParser() {
        parser = new HttpParser();
    }

    /**
     * Check a valid request.
     */
    @Test
    public void setHttpRequestWithValidUrl() {
        String stringToTest = "GET /index.html h";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }
        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertTrue("Set to Reading HTTP version", state.isReadingHttpVersion());
    }

    /**
     * Check to see which Chars are invalid.
     */
    @Test
    public void setHttpRequestWithInvalidEntity() {
        String stringToTest = "GET /@%@#%*@#() ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertTrue("URL Invalid", request.getRequestMethod().isGetRequest());
        assertTrue("Set to ERROR", state.isErrorState());
    }

    @Test
    public void setHttpRequestWithNoEntityExpectingStillReading() {
        String stringToTest = "GET /";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("", request.getPath());
        assertTrue("url reading method is set but not yet finished.", state.isReadingURI());
    }

    @Test
    public void setHttpRequestWithNoPath() {
        String stringToTest = "GET http://localhost:8080/ ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertEquals("Path is relative", "/", request.getPath());
        assertTrue("url reading method is set and finished.", state.isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithEntityFinishedReading() {
        String stringToTest = "GET / ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNotNull("Relative url found", request.getPath());
        assertTrue("Reading Method set to ERROR", state.isReadingHttpVersion());
    }

    @Test
    public void setHttpRequestWithEntityAndPortFinishedReading() {
        String stringToTest = "GET http://www.google.com:8080/index.php?name=value ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNotNull("error state found:" + request.getStateUrl().name(),request.getPath());
        assertEquals("get name=value", "name=value", request.getQuery());
        assertEquals("port is 8080", 8080, (int) request.getPort());
        assertTrue("Reading Method set to ERROR", state.isReadingHttpVersion());
    }

    //used as an example for the variables.
    @Test
    public void testURL() throws MalformedURLException {
        URL aURL = new URL("http://example.com:80/docs/books/tutorial"
                + "/index.html?name=networking&test=apple;ok=apple2#DOWNLOADING");

        System.out.println("protocol = " + aURL.getProtocol());
        System.out.println("authority = " + aURL.getAuthority());
        System.out.println("host = " + aURL.getHost());
        System.out.println("port = " + aURL.getPort());
        System.out.println("path = " + aURL.getPath());
        System.out.println("query = " + aURL.getQuery());
        System.out.println("filename = " + aURL.getFile());
        System.out.println("ref = " + aURL.getRef());
    }

    /**
     * One space is only allowed, no URI found otherwise.
     * //(HTTP Protocol is very strict.)on what it receives.
     */
    @Test
    public void setHttpRequestWithSpaces() {
        String stringToTest = "GET  ";

        for (int i = 0; i < stringToTest.length(); i++) {
            char c = stringToTest.charAt(i);
            parser.nextCharacter(c);
        }

        HttpRequest request = parser.getRequest();
        HttpState state = request.getState();

        assertNull("No url yet found", request.getPath());
        assertTrue("Reading Method set to ERROR", state.isErrorState());
    }

    @Test
    public void CSSLoadingFileTest(){
        String headersAFirstTime = "HTTP/1.1 200 OK\n" +
                "Cache-Control:no-cache;\n" +
                "Accept:text/css,*/*;q=0.1;\n" +
                "Connection:keep-alive;\n" +
                "User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36;\n" +
                "Referer:http://localhost:8080/;\n" +
                "Sec-Fetch-Site:same-origin;\n" +
                "Host:localhost:8080;\n" +
                "Pragma:no-cache;\n" +
                "DNT:1;\n" +
                "Accept-Encoding:gzip, deflate, br;\n" +
                "Accept-Language:en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7;\n" +
                "Sec-Fetch-Mode:no-cors;\n" +
                "Content-Length: 1977\n" +
                "Content-Type: text/css\n";

        String headersAndBodyAtFirstBoot = "HTTP/1.1 200 OK\n" +
                "Cache-Control:no-cache;\n" +
                "Accept:text/css,*/*;q=0.1;\n" +
                "Connection:keep-alive;\n" +
                "User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36;\n" +
                "Referer:http://localhost:8080/;\n" +
                "Sec-Fetch-Site:same-origin;\n" +
                "Host:localhost:8080;\n" +
                "Pragma:no-cache;\n" +
                "DNT:1;\n" +
                "Accept-Encoding:gzip, deflate, br;\n" +
                "Accept-Language:en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7;\n" +
                "Sec-Fetch-Mode:no-cors;\n" +
                "Content-Length: 1977\n" +
                "Content-Type: text/css\n" +
                "\n" +
                "\n" +
                "*,\n" +
                "*:before,\n" +
                "*:after {\n" +
                "    -moz-box-sizing: border-box;\n" +
                "    -webkit-box-sizing: border-box;\n" +
                "    box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "html {\n" +
                "    font-family: Helvetica, Arial, sans-serif;\n" +
                "    font-size: 100%;\n" +
                "    background: #1EADFF;\n" +
                "}\n" +
                "\n" +
                "#page-wrapper {\n" +
                "    width: 650px;\n" +
                "    background: #FFF;\n" +
                "    padding: 1em;\n" +
                "    margin: 1em auto;\n" +
                "    border-top: 5px solid #00f;\n" +
                "    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.8);\n" +
                "}\n" +
                "\n" +
                "#game-board {\n" +
                "    width: 600px;\n" +
                "    height: 300px;\n" +
                "    max-height: 300px;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "}\n" +
                "\n" +
                "footer {\n" +
                "    margin: 5px auto 5px auto;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "    margin-top: 0;\n" +
                "}\n" +
                "\n" +
                "#status {\n" +
                "    font-size: 0.9rem;\n" +
                "    margin-bottom: 1rem;\n" +
                "}\n" +
                "\n" +
                ".open {\n" +
                "    color: green;\n" +
                "}\n" +
                "\n" +
                ".closed {\n" +
                "    color: red;\n" +
                "}\n" +
                "\n" +
                "#messages {\n" +
                "    list-style: none;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    font-size: 0.95rem;\n" +
                "    height: 300px;\n" +
                "    overflow-y: scroll;\n" +
                "    border: 1px solid #D9D9D9;\n" +
                "    border-radius: 3px;\n" +
                "    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.1);\n" +
                "}\n" +
                "\n" +
                "ul li {\n" +
                "    padding: 0.5rem 0.75rem;\n" +
                "    border-bottom: 1px solid #EEE;\n" +
                "}\n" +
                "\n" +
                "ul li:first-child {\n" +
                "    border-top: 1px solid #EEE;\n" +
                "}\n" +
                "\n" +
                "ul li span {\n" +
                "    display: inline-block;\n" +
                "    width: 90px;\n" +
                "    font-weight: bold;\n" +
                "    color: #999;\n" +
                "    font-size: 0.7rem;\n" +
                "    text-transform: uppercase;\n" +
                "    letter-spacing: 1px;\n" +
                "}\n" +
                "\n" +
                ".sent {\n" +
                "    background-color: #F7F7F7;\n" +
                "}\n" +
                "\n" +
                ".received {\n" +
                "}\n" +
                "\n" +
                "#form-msg {\n" +
                "    margin-top: 1.5rem;\n" +
                "}\n" +
                "\n" +
                "textarea {\n" +
                "    width: 100%;\n" +
                "    padding: 0.5rem;\n" +
                "    font-size: 1rem;\n" +
                "    border: 1px solid #D9D9D9;\n" +
                "    border-radius: 3px;\n" +
                "    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.1);\n" +
                "    min-height: 100px;\n" +
                "    margin-bottom: 1rem;\n" +
                "}\n" +
                "\n" +
                "button {\n" +
                "    display: inline-block;\n" +
                "    border-radius: 3px;\n" +
                "    border: none;\n" +
                "    font-size: 0.9rem;\n" +
                "    padding: 0.6rem 1em;\n" +
                "    color: white;\n" +
                "    margin: 0 0.25rem;\n" +
                "    text-align: center;\n" +
                "    background: #5a6268;\n" +
                "    border-bottom: 1px solid #999;\n" +
                "}\n" +
                "\n" +
                "button[type=\"submit\"] {\n" +
                "    background: #007bff;\n" +
                "    border-bottom: 1px solid #007bff;\n" +
                "}\n" +
                "\n" +
                "button:hover {\n" +
                "    opacity: 0.75;\n" +
                "    cursor: pointer;\n" +
                "}\n" +
                "\n" +
                "\n";

        String headersAtSecondPass = "HTTP/1.1 200 OK\n" +
                "Accept:text/css,*/*;q=0.1;\n" +
                "Connection:keep-alive;\n" +
                "User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36;\n" +
                "Referer:http://localhost:8080/;\n" +
                "Sec-Fetch-Site:same-origin;\n" +
                "Host:localhost:8080;\n" +
                "DNT:1;\n" +
                "Accept-Encoding:gzip, deflate, br;\n" +
                "Accept-Language:en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7;\n" +
                "Sec-Fetch-Mode:no-cors;\n" +
                "Content-Length: 1977\n" +
                "Content-Type: text/css\n";

        String headersAndBodyAtSecondPass = "HTTP/1.1 200 OK\n" +
                "Accept:text/css,*/*;q=0.1;\n" +
                "Connection:keep-alive;\n" +
                "User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36;\n" +
                "Referer:http://localhost:8080/;\n" +
                "Sec-Fetch-Site:same-origin;\n" +
                "Host:localhost:8080;\n" +
                "DNT:1;\n" +
                "Accept-Encoding:gzip, deflate, br;\n" +
                "Accept-Language:en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7;\n" +
                "Sec-Fetch-Mode:no-cors;\n" +
                "Content-Length: 1977\n" +
                "Content-Type: text/css\n" +
                "\n" +
                "\n" +
                "*,\n" +
                "*:before,\n" +
                "*:after {\n" +
                "    -moz-box-sizing: border-box;\n" +
                "    -webkit-box-sizing: border-box;\n" +
                "    box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "html {\n" +
                "    font-family: Helvetica, Arial, sans-serif;\n" +
                "    font-size: 100%;\n" +
                "    background: #1EADFF;\n" +
                "}\n" +
                "\n" +
                "#page-wrapper {\n" +
                "    width: 650px;\n" +
                "    background: #FFF;\n" +
                "    padding: 1em;\n" +
                "    margin: 1em auto;\n" +
                "    border-top: 5px solid #00f;\n" +
                "    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.8);\n" +
                "}\n" +
                "\n" +
                "#game-board {\n" +
                "    width: 600px;\n" +
                "    height: 300px;\n" +
                "    max-height: 300px;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "}\n" +
                "\n" +
                "footer {\n" +
                "    margin: 5px auto 5px auto;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "    margin-top: 0;\n" +
                "}\n" +
                "\n" +
                "#status {\n" +
                "    font-size: 0.9rem;\n" +
                "    margin-bottom: 1rem;\n" +
                "}\n" +
                "\n" +
                ".open {\n" +
                "    color: green;\n" +
                "}\n" +
                "\n" +
                ".closed {\n" +
                "    color: red;\n" +
                "}\n" +
                "\n" +
                "#messages {\n" +
                "    list-style: none;\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    font-size: 0.95rem;\n" +
                "    height: 300px;\n" +
                "    overflow-y: scroll;\n" +
                "    border: 1px solid #D9D9D9;\n" +
                "    border-radius: 3px;\n" +
                "    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.1);\n" +
                "}\n" +
                "\n" +
                "ul li {\n" +
                "    padding: 0.5rem 0.75rem;\n" +
                "    border-bottom: 1px solid #EEE;\n" +
                "}\n" +
                "\n" +
                "ul li:first-child {\n" +
                "    border-top: 1px solid #EEE;\n" +
                "}\n" +
                "\n" +
                "ul li span {\n" +
                "    display: inline-block;\n" +
                "    width: 90px;\n" +
                "    font-weight: bold;\n" +
                "    color: #999;\n" +
                "    font-size: 0.7rem;\n" +
                "    text-transform: uppercase;\n" +
                "    letter-spacing: 1px;\n" +
                "}\n" +
                "\n" +
                ".sent {\n" +
                "    background-color: #F7F7F7;\n" +
                "}\n" +
                "\n" +
                ".received {\n" +
                "}\n" +
                "\n" +
                "#form-msg {\n" +
                "    margin-top: 1.5rem;\n" +
                "}\n" +
                "\n" +
                "textarea {\n" +
                "    width: 100%;\n" +
                "    padding: 0.5rem;\n" +
                "    font-size: 1rem;\n" +
                "    border: 1px solid #D9D9D9;\n" +
                "    border-radius: 3px;\n" +
                "    box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.1);\n" +
                "    min-height: 100px;\n" +
                "    margin-bottom: 1rem;\n" +
                "}\n" +
                "\n" +
                "button {\n" +
                "    display: inline-block;\n" +
                "    border-radius: 3px;\n" +
                "    border: none;\n" +
                "    font-size: 0.9rem;\n" +
                "    padding: 0.6rem 1em;\n" +
                "    color: white;\n" +
                "    margin: 0 0.25rem;\n" +
                "    text-align: center;\n" +
                "    background: #5a6268;\n" +
                "    border-bottom: 1px solid #999;\n" +
                "}\n" +
                "\n" +
                "button[type=\"submit\"] {\n" +
                "    background: #007bff;\n" +
                "    border-bottom: 1px solid #007bff;\n" +
                "}\n" +
                "\n" +
                "button:hover {\n" +
                "    opacity: 0.75;\n" +
                "    cursor: pointer;\n" +
                "}\n" +
                "\n" +
                "\n";

        //Assert.assertEquals(headersAFirstTime, headersAtSecondPass);
        Assert.assertEquals(headersAndBodyAtFirstBoot, headersAndBodyAtSecondPass);
    }
}
