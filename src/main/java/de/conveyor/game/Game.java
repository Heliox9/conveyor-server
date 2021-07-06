package de.conveyor.game;

import java.util.UUID;

/**
 * Instance of a single game. Holds the players and controls the flow of game data.
 */
public class Game {
    private Player player1;
    private Player player2;
    //TODO hold game state

    //TODO functionality

    public Game() {
        // REMOVE Test constructor with basic player generation
        player1 = new Player(new Character());
        player2 = new Player(new Character());
    }

    public Player getPlayer(UUID uuid) {
        if (player1.getUuid().equals(uuid)) return player1;
        if (player2.getUuid().equals(uuid)) return player2;
        System.out.println("No player with UUID " + uuid + " found");
        return null;
    }

}
