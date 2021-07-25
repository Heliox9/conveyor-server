package de.conveyor.server;

import de.conveyor.game.Item;

import java.util.ArrayList;

public class ItemSelection {
    ArrayList<Item> bought;
    ArrayList<Item> selection;
    ArrayList<Item> saved;

    @Override
    public String toString() {
        return "ItemSelection{" +
                "bought=" + bought +
                ", selection=" + selection +
                ", saved=" + saved +
                '}';
    }

    public ItemSelection(ArrayList<Item> selection) {
        this.selection = selection;
        bought = new ArrayList<>();
        saved = new ArrayList<>();
    }

    public void setBought(ArrayList<Item> bought) {
        this.bought = bought;
    }

    public void setSaved(ArrayList<Item> saved) {
        this.saved = saved;
    }

    public ArrayList<Item> getSelection() {
        return selection;
    }

    @Deprecated
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
