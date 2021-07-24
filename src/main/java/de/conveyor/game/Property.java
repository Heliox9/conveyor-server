package de.conveyor.game;


import java.util.Random;

/**
 * Every property has en element
 * damage can be blocked by armor of the same typ
 * flash can only be damage (true damage)
 * each category has a rarity physical 1or2 | flash 3 | rest 2or3
 */
public class Property {
    Element element;
    boolean typ;//false=>armor | true=> damage
    int stat;

    /**
     * generates a fully randomized property
     */
    public Property() {
        //TODO should there be different chances for the rarities based on the item lvl?
        this((int) Math.round(Math.random() * 2) + 1);
    }

    /**
     * generates a randomized property within the rarity
     *
     * @param rarity target rarity
     */
    public Property(int rarity) {
        this(randomElementByRarity(rarity), randomTyp(), rarity);
        if (element == Element.FLASH) typ = true;//set flash to always be damage
    }

    /**
     * generates random elements until the element fits within the rarity
     * 1: only physical
     * 2: everything except FLASH
     * 3: everything except PHYSICAL
     *
     * @param rarity target rarity of the element
     * @return the generated element
     */
    private static Element randomElementByRarity(int rarity) {
        if (rarity == 1) return Element.PHYSICAL;
        Element e;
        while (true) {
            e = Element.random();

            if (rarity == 2 && e != Element.FLASH) return e;
            if (rarity == 3 && e != Element.PHYSICAL) return e;
        }
    }

    /**
     * fully random bool generation
     *
     * @return a random boolean value
     */
    private static boolean randomTyp() {
        return new Random().nextBoolean();
    }

    private final int rarity;

    /**
     * generates Property with random stat based on the input
     * for more information see function calculateStats()
     *
     * @param element Element of the property
     * @param typ     typ of the property (true=damage | false=armor)
     * @param rarity  rarity of the property (values 1-3)
     */
    public Property(Element element, boolean typ, int rarity) {
        this.element = element;
        this.typ = typ;
        this.rarity = rarity;

        calculateStats();
    }

    /**
     * generate a stat based on the set values:
     * Element:
     * | LVL   | Typ   | min-max
     * Physical:
     * | 1     | Damage| 5-10
     * | 1     | Armor | 1-8
     * | 2     | Damage| 11-20
     * | 2     | Armor | 9-15
     * Elements:
     * | 2     | Damage| 11-20
     * | 2     | Armor | 9-15
     * | 3     | Damage| 21-35
     * | 3     | Armor | 16-21
     * Flash:
     * | 3     | Damage| 15-20
     */
    private void calculateStats() {
        // stat generation
        if (element == Element.PHYSICAL) {
            if (typ) {
                if (rarity == 1) stat = (int) Math.round(Math.random() * 5) + 5;
                else stat = (int) Math.round(Math.random() * 9) + 11;
            } else {
                if (rarity == 1) stat = (int) Math.round(Math.random() * 7) + 1;
                else stat = (int) Math.round(Math.random() * 6) + 9;
            }
        } else if (element == Element.FLASH) {
            stat = (int) Math.round(Math.random() * 5) + 15;
        } else {
            // generation for all elemental stats
            if (typ) {
                if (rarity == 2) stat = (int) Math.round(Math.random() * 9) + 11;
                else stat = (int) Math.round(Math.random() * 14) + 21;
            } else {
                if (rarity == 2) stat = (int) Math.round(Math.random() * 6) + 9;
                else stat = (int) Math.round(Math.random() * 5) + 16;
            }
        }
    }

    public enum Element {
        PHYSICAL, EARTH, AIR, WATER, FIRE, FLASH;

        public static Element random() {
            return Element.values()[(int) Math.round(Math.random() * Element.values().length)];
        }
    }
}
