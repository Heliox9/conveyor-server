package de.conveyor.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Data modeled after app-implementation
 */
public class Character {
    private final ArrayList<Property> viewable;
    private int hp;
    private Item helmet;
    private Item gloves;
    private Item armor;
    private Item pants;
    private Item shoes;
    private Item special;
    private Item weapon;
    private ArrayList<Item> saved;
    private int money;

    public Character() {
        hp = 100;
        wipeSaved();
        viewable = new ArrayList<>();
        money = 5;
    }

    public void addMoney(int plus) {
        money += plus;
    }

    public int getRange() {
        if (weapon == null) return 0;
        return weapon.getRange();
    }

    public ArrayList<Item> getSaved() {
        return saved;
    }

    public void setSaved(ArrayList<Item> newSaved) throws InvalidAttributeValueException {
        saved = new ArrayList<>();
        for (Item i : newSaved
        ) {
            saved.add(findPossibleItem(i.getUuid()));
        }
    }

    public void wipeSaved() {
        saved = new ArrayList<>();
    }

    private List<Item> findPossibleItems(List<Item> items) throws InvalidAttributeValueException {
        List<Item> possibles = new ArrayList<>();
        for (Item i : items) {
            possibles.add(findPossibleItem(i.getUuid()));
        }
        return possibles;
    }

    public boolean applyItems(List<Item> items) throws InvalidAttributeValueException {
        List<Item> possible = findPossibleItems(items);
        int cost = 0;
        logger.trace("calculating total cost for: " + possible);
        for (Item i : possible) {
            cost += i.getCost();
        }

        if (cost <= money) {
            logger.trace("applying bought items");
            for (Item i : possible) {
                applyItem(i);
            }
            logger.trace("adjusting money balance");
            money -= cost;
            return true;
        } else return false;
    }

    private static Logger logger = LoggerFactory.getLogger(Character.class);

    private Item findPossibleItem(UUID uuid) throws InvalidAttributeValueException {

        for (Item i : possibleItems
        ) {
            logger.trace("uuid: " + uuid + "|" + i.getUuid() + " item");
            if (i.getUuid().equals(uuid)) return i;
        }
        throw new InvalidAttributeValueException("Items uuid does not match any possible item: " + uuid);
    }

    public void lowerHP(int change) {
        if (change > 0) hp -= change;
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

        items.removeAll(Collections.singleton(null));
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
        try {
            Item selected;
            do {
                selected = getAllItems().get((int) Math.round(Math.random() * (getAllItems().size() - 1)));
            } while (selected.getRarity() == 3);

            Item newItem = new Item(0, selected.getItemTyp(), selected.getRarity() + 1);
            applyItem(newItem);
        } catch (Exception e) {
            logger.warn("Wand couldn't upgrade any items");
        }
    }

    private ArrayList<Item> possibleItems;

    public void setPossibleItems(ArrayList<Item> items) {
        this.possibleItems = new ArrayList<>();
        for (Item i : items
        ) {
            possibleItems.add(i.clone());
        }
    }

    public void applyItem(Item item) {
        logger.trace("item to apply: " + item);
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
        item.getProperties().forEach(this::addViewable);
    }


    public int getHp() {
        return hp;
    }

    public Item getSpecial() {
        return special;
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
                ",\n possible items=" + possibleItems +
                '}';
    }


    public int getMoney() {
        return money;
    }
}
