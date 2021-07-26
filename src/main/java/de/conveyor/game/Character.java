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
    private ArrayList<Property> viewable;
    private ArrayList<Item> saved;
    private int money;

    public Character() {
        hp = 100;
        wipeSaved();
        viewable = new ArrayList<>();
        money = 10;//TODO change to actual value
    }

    public void addMoney(int plus) {
        money += plus;
    }

    public int getRange() {
        return weapon.getRange();
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

    public boolean applyItems(List<Item> items) {
        int cost = 0;
        for (Item i : items) {
            cost += i.getCost();
        }
        if (cost < money) {
            for (Item i : items) {
                applyItem(i);
            }
            return true;
        } else return false;
    }

    public void lowerHP(int change) {
        hp -= change;
    }

    public void destroySpecial() {
        special = null;
    }

    public ArrayList<Property> getArmorProperties() {
        return getPropertiesByTyp(false);
    }

    public ArrayList<Property> getDamageProperties() {
        return getPropertiesByTyp(true);
    }

    private ArrayList<Property> getPropertiesByTyp(boolean typ) {
        ArrayList<Property> properties = new ArrayList<>();

        for (Item i : getAllItems()
        ) {
            properties.addAll(addIfFitting(i, typ));
        }

        return properties;
    }

    private ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(helmet);
        items.add(gloves);
        items.add(armor);
        items.add(pants);
        items.add(shoes);
        items.add(special);
        items.add(weapon);
        return items;
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

    private void upgradeItem() {
        Item selected;
        do {
            selected = getAllItems().get((int) Math.round(Math.random() * (getAllItems().size() - 1)));
        } while (selected.getRarity() == 3);

        Item newItem = new Item(0, selected.getItemTyp(), selected.getRarity() + 1);
        applyItem(newItem);
    }

    public void applyItem(Item item) {
        switch (item.getItemTyp()) {
            case SPECIAL:
                if (item.getRarity() == 2) upgradeItem();
                else special = item;
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
        addViewable(item);
    }

    /**
     * add single property to viewable list, only if its element and typ combination is unique
     *
     * @param prop the property to add
     */
    private void addViewable(Property prop) {
        for (Property p : viewable
        ) {
            if (p.element == prop.element && p.typ == prop.typ) return;
        }

        viewable.add(prop);
    }

    /**
     * add all unique properties of the item
     *
     * @param item the item to pull properties from
     */
    private void addViewable(Item item) {
        item.getProperties().forEach(property -> addViewable(property));
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
