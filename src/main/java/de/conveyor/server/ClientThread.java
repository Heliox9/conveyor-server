package de.conveyor.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {


    Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private Logger logger;


    public ClientThread(Socket client) {
        logger = LoggerFactory.getLogger(getClass());
        logger.debug("Client thread creating");
        this.client = client;
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public String read() throws IOException {
        String incoming = in.readLine();
        logger.trace("incoming: " + incoming);
        return incoming;
    }

    public void write(String msg) {
        logger.trace("outgoing" + msg);
        out.println(msg);
    }


    @Override
    public void run() {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            logger.warn("unable to register streams for socket", e);
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
