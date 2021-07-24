package de.conveyor.game;

import com.google.gson.Gson;
import de.conveyor.server.Client;
import de.conveyor.server.ClientThread;
import de.conveyor.server.ItemSelection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Instance of a single game. Holds the players and controls the flow of game data.
 */
public class Game extends Thread {
    static int totalGames;
    private final Gson gson = new Gson();
    int id;
    ArrayList<Client> players;
    private int roundCounter = 1;
    public Game() {
        id = totalGames;
        totalGames++;
        players = new ArrayList<>();
    }

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
        // sending opponent name
        players.get(0).getThread().write("Opponent: " + players.get(1).getName());
        players.get(1).getThread().write("Opponent: " + players.get(0).getName());

        int numPossibleItems = 5;

        // loop fight mechanics
        while (players.get(0).getCharacter().getHp() > 0 && players.get(1).getCharacter().getHp() > 0) { //check for game end

            players.forEach((p) -> {
                //generate items
                ArrayList<Item> set = p.getCharacter().getSaved();
                for (int i = 0; i < numPossibleItems - p.getCharacter().getSaved().size(); i++) {
                    set.add(new Item(roundCounter));
                }
                p.getCharacter().wipeSaved();

                //send items to players
                p.getThread().write(gson.toJson(set));
            });

            players.forEach(p -> {
                // receive buy and safe selection
                try {
                    ItemSelection selection = gson.fromJson(p.getThread().read(), ItemSelection.class);
                    // apply items to characters
                    p.getCharacter().applyItems(selection.getBought());
                    p.getCharacter().setSaved(selection.getSaved());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TODO at some point maybe | calculate cash

            });
            // calculate fight
            calculateAllDamage(players);
            // TODO send fight stats
            roundCounter++;
        }
        // TODO finish gracefully
    }

    private void calculateAllDamage(ArrayList<Client> players) {
        players.get(1).setCharacter(calculateDamage(players.get(0).getCharacter(), players.get(1).getCharacter()));
        players.get(0).setCharacter(calculateDamage(players.get(1).getCharacter(), players.get(0).getCharacter()));
    }

    private Character calculateDamage(Character attacker, Character defender) {
        ArrayList<Property> armorProperties = defender.getArmorProperties();
        ArrayList<Property> attackProperties = attacker.getArmorProperties();

        int damage;
        for (Property attack : attackProperties) {
            damage = attack.stat;
            for (Property armor : armorProperties) {
                if (armor.element == attack.element) damage -= armor.stat;
            }
            defender.lowerHP(damage);
        }
        return defender;
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
