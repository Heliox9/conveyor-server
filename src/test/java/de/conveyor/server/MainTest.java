package de.conveyor.server;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class MainTest {

    @Test
    public void givenClient1_whenServerResponds_thenCorrect() throws IOException {
        Thread t = new Thread(() -> {
            Socket client1 = null;
            try {
                client1 = new Socket("127.0.0.1", 88);

                PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
                out.println("Test 1");
                in.readLine();
                in.readLine();
                out.println("1");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();


        Socket client1 = null;
        try {
            client1 = new Socket("127.0.0.1", 88);

            PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));

            // Enter Player name
            out.println("Test 2");
            System.out.println(in.readLine());
            System.out.println(in.readLine());
            out.println("2");

            out.println(".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}