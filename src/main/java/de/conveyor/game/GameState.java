package de.conveyor.game;

public class GameState {
    int roundNumber;
    Character player;// the player the
    Character opponent;

    public GameState(int roundNumber, Character player, Character opponent) {
        this.roundNumber = roundNumber;
        this.player = player;
        this.opponent = opponent;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Character getPlayer() {
        return player;
    }

    public Character getOpponent() {
        return opponent;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "roundNumber=" + roundNumber +
                ", player=" + player +
                ", opponent=" + opponent +
                '}';
    }
}
