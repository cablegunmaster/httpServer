package unitTest.socket;

import com.jasper.model.http.models.HttpParser;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

public class secWebsocketTestAcceptTest {

    private HttpParser requestParser = new HttpParser();

    @Test
    public void secSha1EncodeBase64Test() {
        //Test to see if the string is the same as the response and the correct salt is used.
        Assert.assertEquals("HSmrc0sMlYUkAGmm5OPpG2HaGWk=",
                requestParser.encodeWebsocketAccept("x3JJHMbDL1EzLkh9GBhXDw=="));
    }

    @Test
    public void secTestValidKey() {
        String key = "LKF8lHGznbKGIgO1UzAOhg==";
        Assert.assertEquals("vxUU43EIDuR3gscJp2fMckI95cQ=", requestParser.encodeWebsocketAccept(key));
    }

    @Test
    public void mozillaKeyExampleTest() {
        String key = "dGhlIHNhbXBsZSBub25jZQ==";
        Assert.assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", requestParser.encodeWebsocketAccept(key));
    }

    @Test
    public void testStrLenSha1() {
        String input = "LKF8lHGznbKGIgO1UzAOhg==";
        byte[] sha1 = DigestUtils.sha1(input + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
        Assert.assertEquals(sha1.length, 20);
    }

    @Test
    public void testEncodeBase() {
        String input = "LKF8lHGznbKGIgO1UzAOhg==";
        byte[] sha1 = DigestUtils.sha1(input + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
        Assert.assertEquals(sha1.length, 20);
    }

}
