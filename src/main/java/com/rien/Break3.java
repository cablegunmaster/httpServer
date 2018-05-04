package com.rien;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * User: rnentjes
 * Date: 26-1-18
 * Time: 13:53
 */
public class Break3 {
    public final static Charset UTF8 = Charset.forName("UTF-8");

    public static void main(String[] args) {
        int port = 8081;

        try{
            int count = 0;

            while(count++ < 5) {
                System.out.println("Connection "+count);

                Socket socket = new Socket("localhost", port);
                OutputStream out = new BufferedOutputStream(socket.getOutputStream());
                out.write('X');
            }

            System.out.println("Server did ok! But why is my computer so busy!?");
        } catch  (Throwable e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
