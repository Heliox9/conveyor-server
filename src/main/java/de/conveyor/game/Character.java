package de.conveyor.game;

/**
 * Data modeled after app-implementation
 */
public class Character {
    private int hp;

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

    public Character() {
        hp = 100;
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

    public Object getPropertiesKnown() {
        return propertiesKnown;
    }

    private Item helmet;
    private Item gloves;
    private Item armor;
    private Item pants;
    private Item shoes;
    private Item special;
    private Item weapon;
    private Object propertiesKnown;//TODO find out what this is

    //TODO fill data
}