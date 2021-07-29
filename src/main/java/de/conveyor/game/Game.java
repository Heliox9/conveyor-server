package de.conveyor.game;

import com.google.gson.Gson;
import de.conveyor.server.Client;
import de.conveyor.server.ClientThread;
import de.conveyor.server.ItemSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.InvalidAttributeValueException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Instance of a single game. Holds the players and controls the flow of game data.
 */
public class Game extends Thread {
    static int totalGames;
    private final Gson gson = new Gson();
    private final Logger logger;
    int id;
    ArrayList<Client> players;
    private int roundCounter = 1;

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

                // sending initialzed character data
//                p.getThread().write(gson.toJson(p.getCharacter()));


            } catch (IOException e) {
                logger.error("failed to send or receive from client", e);
            }
        });
        // sending opponent name
        players.get(0).getThread().write("Opponent: " + players.get(1).getName());
        players.get(1).getThread().write("Opponent: " + players.get(0).getName());
        logger.debug("sent and received names and game id");

        int numPossibleItems = 5;

        boolean error = false;
        // loop fight mechanics
        logger.debug("starting round loop");
        while (!error && players.get(0).getCharacter().getHp() > 0 && players.get(1).getCharacter().getHp() > 0) { //check for game end
            logger.info("round: " + roundCounter);

            players.forEach((p) -> {
                // sending character sheet
//                logger.debug("sending character: " + p.getCharacter());
//                p.getThread().write(gson.toJson(p.getCharacter()));

                logger.info("generating items for player " + p);
                //generate items
                ArrayList<Item> set = new ArrayList<>();
                for (int i = 0; i < (numPossibleItems - p.getCharacter().getSaved().size()); i++) {
                    set.add(new Item(roundCounter));
                }
                logger.trace("num items generated: " + set.size());
                set.addAll(p.getCharacter().getSaved());
                logger.trace("num items total: " + set.size());
                p.getCharacter().wipeSaved();
                logger.trace("set possible items for character: " + set);
                p.getCharacter().setPossibleItems(set);

                //send items to players
                p.getThread().write(gson.toJson(new ItemSelection(set)));
                logger.debug("sent items: " + set);

                // sending current cash balance
                p.getThread().write(gson.toJson(p.getCharacter().getMoney()));

                // sending character sheet
                p.getThread().write(gson.toJson(p.getCharacter()));
            });

            for (Client p : players) {
                // receive buy and safe selection
                try {
                    ItemSelection selection = gson.fromJson(p.getThread().read(), ItemSelection.class);
                    logger.debug("received selection: \n" + selection);
                    // apply items to characters
                    boolean canBuy = p.getCharacter().applyItems(selection.getBought());
                    if (!canBuy) {
                        // TODO add possibility to decline buy
                    }
                    p.getCharacter().setSaved(selection.getSaved());
                    logger.info("selection applied for player: \n" + selection + "\n" + p);
                } catch (IOException e) {
                    logger.error("failed to send or receive from client", e);
                    error = true;
                    break;
                } catch (InvalidAttributeValueException e) {
                    logger.error("player" + p + " attempted to select invalid item", e);
                    error = true;
                    break;
                }


            }
            // calculate fight
            logger.info("calculating damage");
            calculateAllDamage(players);
            // send fight stats
            players.forEach(player -> {
                        GameState state = new GameState(roundCounter, player.getCharacter(), players.get((players.indexOf(player) + 1) % 2).getCharacter());
                        player.getThread().write(gson.toJson(state));
                        logger.debug("sent state: " + state);
                        // destroy special items
                        logger.debug("destroying special: " + player.getCharacter().getSpecial());
                        player.getCharacter().destroySpecial();

                        logger.debug("adding cash to player: " + player);
                        player.getCharacter().addMoney(3);
                    }

            );


            //iterate round
            logger.info("round finished: " + roundCounter);
            roundCounter++;
        }
        logger.info("Game: " + id + " finished after " + roundCounter + " rounds\n" + this);
        // finish gracefully

        logger.info("disconnecting clients");
        players.forEach(p ->

        {
            try {
                p.getThread().close();
            } catch (IOException e) {
                logger.warn("socket closing failed for player p\n", e);
            }
        });
    }

    /**
     * calculate damage for faster player attacking and reverse if necessary
     *
     * @param players the players in the game
     */
    private void calculateAllDamage(ArrayList<Client> players) {

        // determine first attacker
        int faster = 0;
        if (players.get(1).getCharacter().getRange() > players.get(0).getCharacter().getRange())
            faster = 1;
        int slower = (faster + 1) % 2;
        Character damaged;

        // iterate both attacks
        for (int i = 0; i < 2; i++) {
            if (players.get(0).getCharacter() == players.get(1).getCharacter())
                logger.error("CHARACTERS ARE THE SAME, WTF");

            // determine numbering for each round
            faster = (faster + i) % 2;
            slower = (slower + i) % 2;

            // get roles
            Character defender = players.get(slower).getCharacter();
            Character attacker = players.get(faster).getCharacter();

            // check for shield
            int swapped = 0;
            if (defender.getSpecial() != null && defender.getSpecial().getRarity() == 3) {
                defender.destroySpecial();//destroy shield on use
                defender = attacker;//reflect damage on attacker if defender has shield
                swapped = 1;
            }

            // apply damage to player
            damaged = calculateDamage(defender, attacker);

            // skip second round if player died
            if (damaged.getHp() <= 0) {
                logger.info("skipping second attack because player died");
                break;
            }
        }
    }

    /**
     * calculate the damage for one fight
     *
     * @param attacker the character to get offence properties from
     * @param defender the character to get defence properties from
     * @return the defender with adjusted hp value
     */
    private Character calculateDamage(Character attacker, Character defender) {

        ArrayList<Property> armorProperties = collapseProperties(defender.getArmorProperties());
        ArrayList<Property> attackProperties = collapseProperties(attacker.getDamageProperties());
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

    /**
     * creates a list of unique properties with combined stats
     *
     * @param incoming the list of individual properties
     * @return a unique list, with added stats for multiples of the same typ
     */
    private ArrayList<Property> collapseProperties(ArrayList<Property> incoming) {
        ArrayList<Property> collapsed = new ArrayList<>();

        boolean exists;
        for (Property i : incoming
        ) {
            exists = false;
            for (Property c : collapsed
            ) {
                if (c.typ == i.typ && c.element == i.element) {
                    exists = true;
                    c.stat += i.stat;
                    break;
                }
            }
            if (!exists) collapsed.add(i.clone());
        }


        return collapsed;
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
