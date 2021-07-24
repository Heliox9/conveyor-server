package de.conveyor.server;

import de.conveyor.game.Item;

import java.util.ArrayList;

public class ItemSelection {
    ArrayList<Item> bought;
    ArrayList<Item> saved;

    public ItemSelection(ArrayList<Item> bought, ArrayList<Item> saved) {
        this.bought = bought;
        this.saved = saved;
    }

    public ArrayList<Item> getBought() {
        return bought;
    }

    public ArrayList<Item> getSaved() {
        return saved;
    }
}
