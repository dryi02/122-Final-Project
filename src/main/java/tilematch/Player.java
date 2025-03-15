package tilematch;

/**
 * The Player class represents the player in the game.
 * It tracks player statistics and handles player-specific actions.
 */
public class Player {
    private String name;
    private int score;
    private int level;
    private int linesCleared;

    /**
     * Creates a new Player with default settings.
     */
    public Player() {
        this.name = "Player";
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
    }

    /**
     * Creates a new Player with the specified name.
     *
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
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
     * Adds cleared lines to the player's total and potentially levels up.
     *
     * @param lines The number of lines cleared
     */
    public void addLinesCleared(int lines) {
        this.linesCleared += lines;
        checkLevelUp();
    }

    /**
     * Checks if the player should level up based on lines cleared.
     */
    private void checkLevelUp() {
        // Level up every 10 lines
        int newLevel = (linesCleared / 10) + 1;
        if (newLevel > level) {
            level = newLevel;
        }
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
     * Gets the player's level.
     *
     * @return The player's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the number of lines cleared by the player.
     *
     * @return The number of lines cleared
     */
    public int getLinesCleared() {
        return linesCleared;
    }

    /**
     * Resets the player's statistics.
     */
    public void reset() {
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
    }
}