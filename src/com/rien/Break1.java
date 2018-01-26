package com.rien;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * User: rnentjes
 * Date: 26-1-18
 * Time: 13:53
 */
public class Break1 {
    public final static Charset UTF8 = Charset.forName("UTF-8");

    public static void main(String[] args) {
        int port = 8000;
        Random random = new Random(System.nanoTime());

        try{
            Socket socket = new Socket("localhost", port);
            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            int count = 1;

            while(count > 0) {
                byte b = (byte)(random.nextInt() % 256);
                if (b != 10 && b != 13) {
                    out.write(b);
                }
            }

            out.write("GET / HTTP 1.1\r\n\r\n".getBytes(UTF8));
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            String line;
            while((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: kq6py");
            System.exit(1);
        } catch  (IOException e) {
            System.out.println("No I/O");
            System.exit(1);
        }

    }
}
