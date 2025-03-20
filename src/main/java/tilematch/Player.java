package tilematch;

/**
 * The Player class represents the player in the game.
 * It tracks player statistics and handles player-specific actions.
 */
public class Player {
    private String name;
    private int score;

    /**
     * Creates a new Player with default settings.
     */
    public Player() {
        this.name = "Player";
        this.score = 0;
    }

    /**
     * Creates a new Player with the specified name.
     *
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    /**
     * Adds points to the player's score.
     *
     * @param points The points to add
     */
    public void addScore(int points) {
        this.score += points;
    }


    /**
     * Gets the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player's name.
     *
     * @param name The new player name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the player's score.
     *
     * @return The player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Resets the player's statistics.
     */
    public void reset() {
        this.score = 0;
    }
}