package de.conveyor.server;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.EmptyStackException;
import java.util.Queue;
import java.util.Stack;


public class ClientThread extends Thread {

    Socket client;
    Stack<String> incoming = new Stack<>();

    public ClientThread(Socket client) {
        System.out.println("Client thread creating");
        this.client = client;
    }

    public int getMessageBufferSize() {
        return incoming.size();
    }

    public String getNextIncoming() throws EmptyStackException {
        return incoming.pop();
    }

    // TODO get functions with from json building

    // TODO allow sending messages. Is exposing the out writer enough?

    private BufferedReader in = null;
    private PrintWriter out = null;
    private Gson gson = new Gson();

    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void run() {

        try {
            out = new PrintWriter(client.getOutputStream(), true);

            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                incoming.push(inputLine);
                //TODO check message handling
                System.out.println(toString());
            }

            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ClientThread{" +
                "client=" + client +
                ", incoming=" + incoming +
                '}';
    }
}
