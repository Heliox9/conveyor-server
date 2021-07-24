package de.conveyor.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {


    Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;


    // TODO get functions with from json building

    // TODO allow sending messages. Is exposing the out writer enough?

    public ClientThread(Socket client) {
        System.out.println("Client thread creating");
        this.client = client;
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public String read() throws IOException {
        return in.readLine();
    }

    public void write(String msg) {
        out.println(msg);
    }


    @Override
    public void run() {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            //Dummy
        }
    }

    @Override
    public String toString() {
        return "ClientThread{" +
                "client=" + client +
                '}';
    }
}
