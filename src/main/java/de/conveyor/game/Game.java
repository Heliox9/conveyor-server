package de.conveyor.game;

import de.conveyor.server.Client;
import de.conveyor.server.ClientThread;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//TODO probably needs to be a thread in order to check the statues of Client queues and act accordingly

/**
 * Instance of a single game. Holds the players and controls the flow of game data.
 */
public class Game extends Thread {
    @Override
    public void run() {
        players.forEach((p) -> {
            try {
                // Reading names
                p.setName(p.getThread().read());
                System.out.println(p.getName());

                // sending Game id
                p.getThread().write("Game ID: " + id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        players.get(0).getThread().write("Opponent: " + players.get(1).getName());
        players.get(1).getThread().write("Opponent: " + players.get(0).getName());

        while (players.get(0).getThread().isConnected() && players.get(1).getThread().isConnected()) {
            // TODO iterative game state updates
        }
    }

    static int totalGames;
    int id;
    ArrayList<Client> players;
    //TODO hold game state

    //TODO functionality

    public Game() {
        id = totalGames;
        totalGames++;
        players = new ArrayList<>();
    }

    @Override
    public synchronized void start() {
        super.start();
        System.out.println("Started game " + id);
        players.forEach((p) -> {
            if (p.getThread().getState() == State.NEW) p.getThread().start();
        });
    }


    public boolean addClient(Socket socket) {
        if (players.size() > 2) {
            System.out.println("Player limit for game already reached!!");
            return false;
        }
        ClientThread client = new ClientThread(socket);
        client.start();
        players.add(new Client(client));
        if (players.size() == 2) start();
        return true;
    }

    public boolean isFull() {
        return players.size() == 2;
    }

}
