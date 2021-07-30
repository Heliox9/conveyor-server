package de.conveyor.game;

import javax.management.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Model for a single item. Needs to match generation and types of app-implementation
 */
public class Item {
    private final int round;
    private final int rarity;
    private final ArrayList<Property> properties;
    private final ItemTyp itemTyp;
    private int cost;
    private int range;
    private final UUID uuid;

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
     * @param itemTyp the itemTyp to generate
     */
    public Item(int round, ItemTyp itemTyp, int rarity) {
        uuid = UUID.randomUUID();//uniquely mark the item for later lookup
        this.itemTyp = itemTyp;
        this.round = round;
        if (rarity < 1) {
            try {
                rarity = calculateRarityByRound();
            } catch (InvalidAttributeValueException e) {
                e.printStackTrace();
            }
        }
        this.rarity = rarity;

        //set cost based on rarity
        cost = switch (rarity) {
            case 1 -> 5;
            case 2 -> 15;
            case 3 -> 25;
            default -> throw new IllegalStateException("Unexpected value: " + rarity);
        };

        properties = new ArrayList<>();
        if (itemTyp == ItemTyp.WEAPON) {
            //0 range 1 only
            //2 range 1,2 only
            //other ranges all rarities
            do {
                range = (int) Math.round(Math.random() * 3) * 2;
            } while ((range == 0 && rarity > 1) || (range == 2 && rarity == 3));
        }
        if (itemTyp == ItemTyp.SPECIAL && rarity == 1) {
            properties.add(new Property(2));
        } // Set some property
        // Rar1 potion  : extra prop for 1 turn (rar 2 prop)
        // Rar2 wand    : 1 random item gets replaced with 1 tier higher / rerolled if slot empty or already rar3
        // Rar3 shield  : reflects all dmg for 1 turn then breaks (attacker still blocks with his def items)
        else generateStandard(); // generate none special item
    }

    private Item(int round, int rarity, ArrayList<Property> properties, ItemTyp itemTyp, int cost, int range, UUID uuid) {
        this.round = round;
        this.rarity = rarity;
        this.properties = properties;
        this.itemTyp = itemTyp;
        this.cost = cost;
        this.range = range;
        this.uuid = uuid;
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
        int numProperties = (int) Math.round(Math.random()) + (rarity == 1 ? 1 : 3);//generate num between 1-4

        if (rarity == 3) {
            // generate flash property
            properties.add(new Property(Property.Element.FLASH, true, 3));
            numProperties--;
        }

        // add all other properties
        Property newProp;
        while (properties.size() < numProperties) {
            newProp = new Property(rarity);
            // add fully random property
            if (!checkPropertyExists(newProp)) properties.add(newProp);
        }
    }

    private boolean checkPropertyExists(Property check) {
        for (Property p : properties
        ) {
            if (p.typ == check.typ && p.element == check.element) return true;
        }
        return false;
    }

    private int calculateRarityByRound() throws InvalidAttributeValueException {
        int rarity;
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
     * @param chanceRareOne percentage for tier 1
     * @param chanceRareTwo percentage for tier 2
     * @return tier between 1-3
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

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Item{" +
                "uuid=" + uuid +
                ", rarity=" + rarity +
                ", itemTyp=" + itemTyp +
                ", cost=" + cost +
                ", range=" + range +
                ", properties=" + properties +
                '}';
    }

    @Override
    protected Item clone() {
        return new Item(round, rarity, properties, itemTyp, cost, range, uuid);
    }
}
