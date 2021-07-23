package de.conveyor.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    Gson gson = new Gson();

    @Test
    public void givenClient1_whenServerResponds_thenCorrect() throws IOException {
        Socket client1 = new Socket("127.0.0.1", 88);
        PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        out.println("hello");
        String back1 = in.readLine();
//        System.out.println(gson.fromJson(back1, SampleObject.class));
        out.println("1");
        String back2 = in.readLine();
//        System.out.println(gson.fromJson(back2, SampleObject.class));

        out.println(".");
        assertEquals("C1hello", back1);
        assertEquals("C1world", back2);

        // Notes: all responses need to be pulled. if not then it will just work throught the queue and therefore be old data
    }

    @Test
    public void givenClient2_whenServerResponds_thenCorrect() throws IOException {
        Socket client1 = new Socket("127.0.0.1", 88);
        PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        out.println("hello");
        String back1 = in.readLine();
//        System.out.println(gson.fromJson(back1, SampleObject.class));
        out.println("2.5");
        String back2 = in.readLine();
//        System.out.println(gson.fromJson(back2, SampleObject.class));
        out.println(".");
        assertEquals("C2hello", back1);
        assertEquals("C2world", back2);

    }


}