package integrationTest.rien;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * User: rnentjes
 * Date: 26-1-18
 * Time: 13:53
 */
public class Break4 {
    public final static Charset UTF8 = Charset.forName("UTF-8");

    public static void main(String[] args) {
        int port = 8081;

        try{
            int count = 0;

            System.out.println("Connection "+count);

            Socket socket = new Socket("localhost", port);
            OutputStream out = socket.getOutputStream();
            out.write(0xc1);
            out.write(0xc1);
            out.write(0xc1);
            out.write(0xc1);
            out.write(0xc1);
            out.write(13);
            out.write(10);
            out.write(13);
            out.write(10);

            out.flush();

            System.out.println("Server dead yet?");
        } catch  (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
