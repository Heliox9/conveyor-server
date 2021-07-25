package de.conveyor.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Data modeled after app-implementation
 */
public class Character {
    private int hp;
    private Item helmet;
    private Item gloves;
    private Item armor;
    private Item pants;
    private Item shoes;
    private Item special;
    private Item weapon;
    //    private Object propertiesKnown;//TODO find out what this is
    private ArrayList<Item> saved;

    public Character() {
        hp = 100;
        wipeSaved();
    }

    public ArrayList<Item> getSaved() {
        return saved;
    }

    public void setSaved(ArrayList<Item> saved) {
        this.saved = saved;
    }

    public void wipeSaved() {
        saved = new ArrayList<>();
    }

    public void applyItems(List<Item> items) {
        for (Item i : items) {
            applyItem(i);
        }
    }

    public void lowerHP(int change) {
        hp -= change;
    }

    public ArrayList<Property> getArmorProperties() {
        return getPropertiesByTyp(false);
    }

    public ArrayList<Property> getDamageProperties() {
        return getPropertiesByTyp(true);
    }

    private ArrayList<Property> getPropertiesByTyp(boolean typ) {
        ArrayList<Property> properties = new ArrayList<>();

        properties.addAll(addIfFitting(helmet, typ));
        properties.addAll(addIfFitting(gloves, typ));
        properties.addAll(addIfFitting(armor, typ));
        properties.addAll(addIfFitting(pants, typ));
        properties.addAll(addIfFitting(shoes, typ));
        properties.addAll(addIfFitting(special, typ));
        properties.addAll(addIfFitting(weapon, typ));

        return properties;
    }

    private ArrayList<Property> addIfFitting(Item item, boolean typ) {
        ArrayList<Property> properties = new ArrayList<>();

        if (item != null) {
            item.getProperties().forEach(property -> {
                if (property.typ == typ) properties.add(property);
            });
        }

        return properties;
    }

    public void applyItem(Item item) {
        switch (item.getItemTyp()) {
            case SPECIAL:
                special = item;
                break;
            case ARMOR:
                armor = item;
                break;
            case PANTS:
                pants = item;
                break;
            case SHOES:
                shoes = item;
                break;
            case GLOVES:
                gloves = item;
                break;
            case HELMET:
                helmet = item;
                break;
            case WEAPON:
                weapon = item;
                break;
        }
    }

    public int getHp() {
        return hp;
    }

    public Item getHelmet() {
        return helmet;
    }

    public Item getGloves() {
        return gloves;
    }

    public Item getArmor() {
        return armor;
    }

    public Item getPants() {
        return pants;
    }

    public Item getShoes() {
        return shoes;
    }

    public Item getSpecial() {
        return special;
    }

    public Item getWeapon() {
        return weapon;
    }

    @Override
    public String toString() {
        return "Character{" +
                "hp=" + hp +
                ", helmet=" + helmet +
                ", gloves=" + gloves +
                ", armor=" + armor +
                ", pants=" + pants +
                ", shoes=" + shoes +
                ", special=" + special +
                ", weapon=" + weapon +
                ", saved=" + saved +
                '}';
    }

//    public Object getPropertiesKnown() {
//        return propertiesKnown;
//    }

}
