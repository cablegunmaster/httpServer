package com.jasper.integrationtest.rien;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * User: rnentjes
 * Date: 26-1-18
 * Time: 13:53
 */
public class Break1 {
    public static void main(String[] args) {
        int port = 8081;
        Random random = new Random(System.nanoTime());

        try{

            System.out.println("Start break#1");

            Socket socket = new Socket("localhost", port);
            OutputStream out = new BufferedOutputStream(socket.getOutputStream(), 256);

            while(true) {
                byte b = (byte)(random.nextInt() % 256);
                if (b != 10 && b != 13) {
                    out.write(b);
                }
            }
        } catch  (IOException e) {
            System.out.println("Request denied.");

            e.printStackTrace();
            System.exit(1);
        }
    }
}
