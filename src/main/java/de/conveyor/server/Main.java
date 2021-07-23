package de.conveyor.server;

import de.conveyor.game.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    static ServerSocket serverSocket;
    static ArrayList<Game> games;

    public static void main(String[] args) throws IOException {
        // create wrapper objects
        games = new ArrayList<>();
        Socket client;
        Game game = new Game();

        // create server socket
        System.out.println("Server creating");
        serverSocket = new ServerSocket(88);

        boolean up;
        // keep server up
        while (true) {
            System.out.println("Waiting for client");
            // accept client and add to game
            client = serverSocket.accept();
            System.out.println("Client accepted");

            up = game.addClient(client);
            if (!up) break;

            // check if game is full and create new
            if (game.isFull()) {
                games.add(game);
                System.out.println(games);
                game = new Game();
                System.out.println("Creating new game");
            }
        }
        // kills server if client fails to add to game
        serverSocket.close();
    }
}
