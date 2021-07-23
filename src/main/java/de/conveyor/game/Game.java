package de.conveyor.game;

import de.conveyor.server.ClientThread;

import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

//TODO probably needs to be a thread in order to check the statues of Client queues and act accordingly

/**
 * Instance of a single game. Holds the players and controls the flow of game data.
 */
public class Game {
    static int totalGames;
    int id;
    HashMap<ClientThread, Character> players;
    //TODO hold game state

    //TODO functionality

    public Game() {
        id = totalGames;
        totalGames++;
        players = new HashMap<>();
    }


    public boolean addClient(Socket socket) {
        if (players.size() > 2) {
            System.out.println("Player limit for game already reached!!");
            return false;
        }
        ClientThread client = new ClientThread(socket);
        client.start();
        players.put(client, new Character());
        return true;
    }

    public boolean isFull() {
        return players.size() == 2;
    }

}
