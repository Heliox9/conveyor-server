package de.conveyor.game;

import java.util.UUID;

/**
 * A single player modeled for game management. Holds connection data and access, as well as the character data.
 */
public class Player {
    private Character character;
    //TODO hold connection or identification of some sort
    private UUID uuid;

    public Player(Character character) {
        this.character = character;
        uuid = UUID.randomUUID();
    }

    public Character getCharacter() {
        return character;
    }

    public UUID getUuid() {
        return uuid;
    }
}
