package de.conveyor.server;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {

    Socket client;


    public ClientThread(Socket client) {
        System.out.println("Client thread creating");
        this.client = client;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = null;
            PrintWriter out = null;
            Gson gson = new Gson();

            out = new PrintWriter(client.getOutputStream(), true);

            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                //TODO handle messages
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
                '}';
    }
}
