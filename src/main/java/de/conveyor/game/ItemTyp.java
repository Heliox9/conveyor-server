package de.conveyor.game;


public enum ItemTyp {
    HELMET, GLOVES, ARMOR, PANTS, SHOES,
    SPECIAL, WEAPON;

    /**
     * generates a random value from the ItemTyp enum
     *
     * @return random value
     */
    public static ItemTyp random() {
        return ItemTyp.values()[(int) Math.round(Math.random() * (ItemTyp.values().length - 1))];
    }
}
