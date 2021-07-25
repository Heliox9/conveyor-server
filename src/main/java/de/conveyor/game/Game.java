package de.conveyor.game;

import com.google.gson.Gson;
import de.conveyor.server.Client;
import de.conveyor.server.ClientThread;
import de.conveyor.server.ItemSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger logger;

    public Game() {
        id = totalGames;
        logger = LoggerFactory.getLogger("Game" + id);
        totalGames++;
        players = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", roundCounter=" + roundCounter +
                "\nplayers=" + players +
                '}';
    }

    @Override
    public void run() {
        logger.debug("executing game flow");
        players.forEach((p) -> {
            try {
                // Reading names
                p.setName(p.getThread().read());
                logger.debug(p.getName());

                // sending Game id
                p.getThread().write("Game ID: " + id);
            } catch (IOException e) {
                logger.error("failed to send or receive from client", e);
            }
        });
        // sending opponent name
        players.get(0).getThread().write("Opponent: " + players.get(1).getName());
        players.get(1).getThread().write("Opponent: " + players.get(0).getName());
        logger.debug("sent and received names and game id");

        int numPossibleItems = 5;

        // loop fight mechanics
        logger.debug("starting round loop");
        while (players.get(0).getCharacter().getHp() > 0 && players.get(1).getCharacter().getHp() > 0) { //check for game end
            logger.info("round: " + roundCounter);

            players.forEach((p) -> {
                logger.info("generating items for player " + p);
                //generate items
                ArrayList<Item> set = p.getCharacter().getSaved();
                for (int i = 0; i < numPossibleItems - p.getCharacter().getSaved().size(); i++) {
                    set.add(new Item(roundCounter));
                }
                p.getCharacter().wipeSaved();

                //send items to players
                p.getThread().write(gson.toJson(new ItemSelection(set)));
                logger.debug("sent items: " + set);
            });

            players.forEach(p -> {
                // receive buy and safe selection
                try {
                    ItemSelection selection = gson.fromJson(p.getThread().read(), ItemSelection.class);
                    logger.debug("received selection: \n" + selection);
                    // apply items to characters
                    p.getCharacter().applyItems(selection.getBought());
                    p.getCharacter().setSaved(selection.getSaved());
                    logger.info("selection applied for player: \n" + selection + "\n" + p);
                } catch (IOException e) {
                    logger.error("failed to send or receive from client", e);
                }

                // TODO at some point maybe | calculate cash

            });
            // calculate fight
            logger.info("calculating damage");
            calculateAllDamage(players);
            // TODO send fight stats
            players.forEach(player -> {
                        GameState state = new GameState(roundCounter, player.getCharacter(), players.get((players.indexOf(player) + 1) % 2).getCharacter());
                        player.getThread().write(gson.toJson(state));
                        logger.debug("sent state: " + state);
                    }

            );

            //iterate round
            logger.info("round finished: " + roundCounter);
            roundCounter++;
        }
        logger.info("Game: " + id + " finished after " + roundCounter + " rounds\n" + toString());
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
        logger.info("Started game " + id);
        players.forEach((p) -> {
            if (p.getThread().getState() == State.NEW) p.getThread().start();
        });
    }


    public boolean addClient(Socket socket) {
        if (players.size() > 2) {
            logger.warn("Player limit for game already reached!!");
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
