package de.conveyor.game;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;

/**
 * Model for a single item. Needs to match generation and types of app-implementation
 */
public class Item {
    private int round;
    private int rarity;
    private int cost;
    private ArrayList<Property> properties;
    private ItemTyp itemTyp;
    private int range;
    /**
     * Fully random item generation
     */
    public Item(int round) {
        this(round, ItemTyp.random());
    }

    public Item(int round, ItemTyp itemTyp) {
        this(round, itemTyp, -42);
    }

    /**
     * Typ based item generation
     *
     * @param itemTyp
     */
    public Item(int round, ItemTyp itemTyp, int rarity) {
        this.itemTyp = itemTyp;
        this.round = round;
        if (rarity < 1) {
            try {
                rarity = calculateRarityByRound();
            } catch (InvalidAttributeValueException e) {
                e.printStackTrace();
            }
        }

        properties = new ArrayList<>();
        if (itemTyp == ItemTyp.WEAPON) {
            //0 range 1 only
            //2 range 1,2 only
            //other ranges all rarities
            do {
                range = (int) Math.round(Math.random() * 3) * 2;
            } while ((range == 0 && rarity > 1) || (range == 2 && rarity == 3));
        }
        if (itemTyp == ItemTyp.SPECIAL && rarity == 1) properties.add(new Property(2)); // Set some property
            // Rar1 potion: extra prop for 1 turn (rar 2 prop)
            //  Rar2 Wand: 1 random item gets replaced with 1 tier higher / rerolled if slot empty or already rar3
            // rar3 shield: reflects all dmg for 1 turn then breaks (attacker still blocks with his def items)
        else generateStandard(); // generate none special item
    }

    public int getRarity() {
        return rarity;
    }

    public int getCost() {
        return cost;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public ItemTyp getItemTyp() {
        return itemTyp;
    }

    public int getRange() {
        return range;
    }

    private void generateStandard() {
        // calculate number of properties
        int numProperties = (int) Math.round(Math.random() * (rarity == 1 ? 1 : 3)) + 1;//generate num between 1-4

        //set cost based on rarity
        switch (rarity) {
            case 1:
                cost = 5;
                break;
            case 2:
                cost = 15;
                break;
            case 3:
                cost = 25;
                break;
        }

        if (rarity == 3) {
            // generate flash property
            properties.add(new Property(Property.Element.FLASH, true, 3));
            numProperties--;
        }

        // add all other properties
        for (int i = 0; i < numProperties; i++) {
            // add fully random property
            properties.add(new Property(rarity));
        }
    }

    private int calculateRarityByRound() throws InvalidAttributeValueException {
        int rarity = 0;
        switch (round) {
            case 1:
                return 1;
            case 2:
                rarity = calculateRarity(.8, .2);
                break;
            case 3:
                rarity = calculateRarity(.7, .3);
                break;
            case 4:
                rarity = calculateRarity(.6, .4);
                break;
            case 5:
                rarity = calculateRarity(.5, .4);
                break;
            case 6:
                rarity = calculateRarity(.3, .45);
                break;
            case 7:
                rarity = calculateRarity(.1, .5);
                break;
            case 8:
                rarity = calculateRarity(0, .45);
                break;

            default:
                return 3;
        }


        return rarity;
    }

    /**
     * Generates a random number and looks up where it lands in the three rarities
     *
     * @param chanceRareOne
     * @param chanceRareTwo
     * @return
     */
    private int calculateRarity(double chanceRareOne, double chanceRareTwo) throws InvalidAttributeValueException {
        if (chanceRareTwo + chanceRareTwo > 1)
            throw new InvalidAttributeValueException("combined chances cannot be >1 but are " + (chanceRareTwo + chanceRareTwo));

        double random = Math.random();
        if (random <= chanceRareOne) return 1;
        random -= chanceRareOne;
        if (random <= chanceRareTwo) return 2;
        return 3;
    }

    @Override
    public String toString() {
        return "Item{" +
                "rarity=" + rarity +
                ", itemTyp=" + itemTyp +
                ", cost=" + cost +
                ", range=" + range +
                ", properties=" + properties +
                '}';
    }
}
