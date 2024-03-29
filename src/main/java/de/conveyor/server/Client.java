package de.conveyor.server;

import de.conveyor.game.Character;

public class Client {
    private final ClientThread thread;
    private Character character;
    private String name;

    public Client(ClientThread thread) {
        this(thread, new Character());
    }

    public Client(ClientThread thread, Character character) {
        this.thread = thread;
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClientThread getThread() {
        return thread;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", character=" + character +
                ", thread=" + thread +
                '}';
    }
}
